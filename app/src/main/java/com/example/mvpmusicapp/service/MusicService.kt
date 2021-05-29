package com.example.mvpmusicapp.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.example.mvpmusicapp.data.model.Song
import com.example.mvpmusicapp.service.notification.createNotificationChannel
import com.example.mvpmusicapp.service.notification.sendNotification
import com.example.mvpmusicapp.ui.ServiceInterface

class MusicService : Service() {

    private var musicPlayer: MediaPlayer? = null
    private var songList: MutableList<Song>? = null
    private var prgCallback: ServiceInterface? = null
    private var notificationManager: NotificationManager? = null
    private var currentSong = 0
    var isReplay = false

    override fun onBind(p0: Intent?): IBinder = MusicBinder(this)

    override fun onCreate() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        createNotificationChannel(applicationContext, notificationManager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        pushNotification()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelNotification()
        musicPlayer?.release()
        stopSelf()
    }

    fun getIntent() = Intent(applicationContext, MusicService::class.java)

    fun setSongList(songList: MutableList<Song>) {
        this.songList = songList
    }

    fun setProgressInterface(serviceInterface: ServiceInterface) {
        prgCallback = serviceInterface
    }

    fun pushNotification() {
        notificationManager.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForeground(
                1, sendNotification(
                    songList?.get(currentSong), applicationContext, musicPlayer?.isPlaying
                )
            ) else this?.notify(
                1,
                sendNotification(
                    songList?.get(currentSong),
                    applicationContext,
                    musicPlayer?.isPlaying
                )
            )
        }
    }

    fun isMusicPlaying() = musicPlayer?.isPlaying

    fun currentPosition() = musicPlayer?.currentPosition

    fun playMusic() = musicPlayer?.start()

    fun pauseMusic() = musicPlayer?.pause()

    fun resumeMusic() {
        musicPlayer?.currentPosition?.let {
            musicPlayer?.seekTo(it)
            musicPlayer?.start()
        }
    }

    fun switchMusic(index: Int) {
        currentSong = index
        if (musicPlayer?.isPlaying == true) {
            musicPlayer?.release()
        }
        if (applicationContext == null || songList == null) {
            return
        }
        musicPlayer = MediaPlayer.create(applicationContext, songList!![index].uri)
        musicPlayer?.apply {
            isLooping = true
            setOnCompletionListener {
                if (prgCallback != null) {
                    prgCallback?.updateProgress(musicPlayer?.currentPosition)
                    if(duration == currentPosition && !isReplay){
                        pauseMusic()
                        prgCallback?.onReplayMusic()
                    }
                }
            }
        }
        playMusic()
    }

    fun getDuration() = musicPlayer?.duration

    private fun cancelNotification() {
        val notificationManager = ContextCompat.getSystemService(
            this, NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
    }

    class MusicBinder(private val service: MusicService) : Binder() {
        fun getService() = service
    }
}
