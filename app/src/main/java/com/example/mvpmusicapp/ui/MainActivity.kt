package com.example.mvpmusicapp.ui

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import com.example.mvpmusicapp.R
import com.example.mvpmusicapp.data.model.Song
import com.example.mvpmusicapp.data.repository.SongRepository
import com.example.mvpmusicapp.data.source.local.QueryMusicsData
import com.example.mvpmusicapp.databinding.ActivityMainBinding
import com.example.mvpmusicapp.service.MusicService
import com.example.mvpmusicapp.ui.adapter.SongAdapter
import com.example.mvpmusicapp.ui.presenter.SongPresenter
import java.util.*

const val TIME_DELAY = 0L
const val TIME_PERIOD = 1000L

class MainActivity : AppCompatActivity(),
    SongInterface.View,
    View.OnClickListener,
    ServiceInterface,
    SearchView.OnCloseListener,
    SearchView.OnQueryTextListener {

    private val DURATION_DEFAULT = 0
    private val storageRequest = 100
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val musicIntent by lazy { Intent(this, MusicService::class.java) }
    private var songPresenter: SongInterface.Presenter? = null
    private var songs = mutableListOf<Song>()
    private var songAdapter = SongAdapter(this::clickSong, this::longClickSong)
    private var boundService = false
    private var currentSong = 0
    private var musicService: MusicService? = null
    private val deleteSongs = mutableListOf<Song>()
    private var isReplay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initData()
    }

    override fun getAllSongList(songList: List<Song>?) {
        if (songList != null) {
            songs = songList.toMutableList()
        }
        songAdapter.setSongList(songs)
        musicService?.setSongList(songs)
    }

    override fun showErrorMessage() {
        Toast.makeText(this, getString(R.string.text_load_song_failed), Toast.LENGTH_SHORT).show()
    }

    override fun setPlayButton() {
        musicService?.pauseMusic()
        binding.imagePlayMusic.setImageResource(R.drawable.ic_play_button)
    }

    override fun setPauseButton() {
        musicService?.resumeMusic()
        binding.imagePlayMusic.setImageResource(R.drawable.ic_pause)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == storageRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            songPresenter?.getLocalSongs()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imagePlayMusic -> {
                songPresenter?.playMusic(musicService?.isMusicPlaying() == true)
                musicService?.pushNotification()
            }

            R.id.imageForward -> nextSongClick()

            R.id.imageRewind -> prevSongClick()

            R.id.textDestroy -> {
                songAdapter.setCheckboxHide(true)
                binding.apply {
                    imageDelete.visibility = View.GONE
                    textDestroy.visibility = View.GONE
                }
                deleteSongs.clear()
            }
            R.id.imageDelete -> {
                if (deleteSongs.size == 0) return
                songs.removeAll(deleteSongs)
                deleteSongs.forEach {
                    contentResolver.delete(it.uri, null, null)
                }

                songAdapter.setSongList(songs)

            }

            R.id.imageSearch -> {
                binding.apply {
                    searchSong.visibility = View.VISIBLE
                    imageSearch.visibility = View.INVISIBLE
                    textTitle.visibility = View.INVISIBLE
                }
            }

            R.id.imageReplay -> {
                isReplay = !isReplay
                binding.textReplay.visibility = if (!isReplay) View.GONE else View.VISIBLE
                musicService?.isReplay = isReplay
            }
        }
    }

    override fun updateProgress(position: Int?) {
        Thread { binding.progressMusic.progress = position ?: DURATION_DEFAULT }.start()
    }

    override fun onReplayMusic() {
        songPresenter?.playMusic(musicService?.isMusicPlaying() == true)
        musicService?.pushNotification()
    }

    override fun onClose(): Boolean {
        binding.apply {
            imageSearch.visibility = View.VISIBLE
            searchSong.visibility = View.GONE
            textTitle.visibility = View.VISIBLE
        }
        songAdapter.setSongList(songs)
        return false
    }

    override fun onQueryTextSubmit(query: String?) = false

    override fun onQueryTextChange(newText: String?): Boolean {
        val songsFilter = mutableListOf<Song>()
        songs.forEach {
            if (it.name.contains(newText.toString(), ignoreCase = true)) {
                songsFilter.add(it)
            }
        }
        songAdapter.setSongList(songsFilter)
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicReceiver)
        unbindService(serviceConnection)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val serviceBinder = p1 as MusicService.MusicBinder
            musicService = serviceBinder.getService()
            musicService?.setProgressInterface(this@MainActivity)
            boundService = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            boundService = false
        }

    }

    private fun initViews() {
        binding.apply {
            listOf(
                imagePlayMusic, imageForward, imageRewind,
                imageDelete, textDestroy, imageSearch , imageReplay
            ).forEach {
                it.setOnClickListener(this@MainActivity)
            }

            searchSong.apply {
                setOnCloseListener(this@MainActivity)
                setOnQueryTextListener(this@MainActivity)
            }
        }
        binding.recyclerSong.adapter = songAdapter
    }

    private fun setProgressBar() {
        binding.progressMusic.max = musicService?.getDuration() ?: DURATION_DEFAULT
        binding.progressMusic.progress = DURATION_DEFAULT
    }

    private fun initData() {
        songPresenter = SongPresenter(
            SongRepository.getInstance(QueryMusicsData.getInstance(this)), this
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PackageManager.PERMISSION_GRANTED -> songPresenter?.getLocalSongs()
                else -> requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    storageRequest
                )
            }
        }
        bindService(musicIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        registerReceiver(musicReceiver, IntentFilter(getString(R.string.intent_action)))
    }

    private fun setConstraintMusicDialog() {
        binding.apply {
            cardPlayMusic.visibility = View.VISIBLE
            imagePlayMusic.setImageResource(R.drawable.ic_pause)
            updateMusicName()
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraintMainParent)
        constraintSet.connect(
            R.id.recyclerSong,
            ConstraintSet.BOTTOM,
            R.id.cardPlayMusic,
            ConstraintSet.TOP
        )
        constraintSet.applyTo(binding.constraintMainParent)
    }

    private fun updateMusicName() {
        binding.apply {
            textMusicName.text = songs[currentSong].name
            textMusicInfor.text = resources.getString(
                R.string.text_music_infor,
                convertToDurationFormat(
                    this@MainActivity,
                    musicService?.getDuration()?.toDouble()
                ),
                songs[currentSong].artist
            )
        }
    }

    private fun updateProgress() {
        val musicTime = Timer()
        musicTime.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (musicService?.currentPosition()!! < musicService?.getDuration()!!) {
                        binding.progressMusic.progress =
                            musicService?.currentPosition() ?: DURATION_DEFAULT
                    } else {
                        binding.progressMusic.progress = DURATION_DEFAULT
                        musicTime.cancel()
                        musicTime.purge()
                    }
                }
            }

        }, TIME_DELAY, TIME_PERIOD)
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(musicService?.getIntent())
        else startService(musicService?.getIntent())
    }

    private fun nextSongClick() {
        currentSong = if (currentSong < songs.size - 1) currentSong + 1 else 0
        musicService?.switchMusic(currentSong)
        updateMusicName()
        musicService?.pushNotification()
    }

    private fun prevSongClick() {
        currentSong = if (currentSong > 0) currentSong - 1 else songs.size - 1
        musicService?.switchMusic(currentSong)
        updateMusicName()
        musicService?.pushNotification()
    }

    private var musicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.extras?.getString(getString(R.string.intent_data_extra))) {
                getString(R.string.intent_play_music) -> {
                    songPresenter?.playMusic(musicService?.isMusicPlaying() == true)
                    musicService?.pushNotification()
                }

                getString(R.string.intent_next_music) -> nextSongClick()

                getString(R.string.intent_prev_music) -> prevSongClick()
            }
        }

    }

    private fun clickSong(position: Int?) {
        if (position != null) {
            currentSong = position
        }
        musicService?.switchMusic(currentSong)
        musicService?.playMusic()
        startService()
        setConstraintMusicDialog()
        setProgressBar()
        updateProgress()
    }

    private fun longClickSong(song: Song?) {
        binding.apply {
            textDestroy.visibility = View.VISIBLE
            imageDelete.visibility = View.VISIBLE
        }
        if (song?.isSelected == true) {
            deleteSongs.add(song)
        } else if (deleteSongs.contains(song)) {
            deleteSongs.remove(song)
        }

    }
}
