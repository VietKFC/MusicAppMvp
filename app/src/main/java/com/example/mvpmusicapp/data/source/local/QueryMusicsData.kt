package com.example.mvpmusicapp.data.source.local

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.example.mvpmusicapp.data.model.Song
import com.example.mvpmusicapp.data.source.MusicsDataSource
import com.example.mvpmusicapp.data.source.local.task.LocalAsyncTask
import com.example.mvpmusicapp.data.source.local.task.OnResultCallback

class QueryMusicsData(private val context: Context) : MusicsDataSource {

    override fun getData(callback: OnResultCallback) {
        LocalAsyncTask({ getSongs() }, callback).execute()
    }

    private fun getSongs(): List<Song> {
        val songList = mutableListOf<Song>()

        val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.Media.DURATION
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val audioCursor = context.contentResolver.query(
            contentUri,
            projection,
            selection,
            null,
            null
        )

        while (audioCursor?.moveToNext() == true) {
            songList.add(Song(audioCursor))
        }

        return songList
    }

    companion object {
        private var instance: QueryMusicsData? = null
        fun getInstance(context: Context) =
            instance ?: QueryMusicsData(context).also { instance = it }
    }
}
