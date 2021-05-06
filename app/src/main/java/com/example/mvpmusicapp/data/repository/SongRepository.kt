package com.example.mvpmusicapp.data.repository

import com.example.mvpmusicapp.data.source.MusicsDataSource
import com.example.mvpmusicapp.data.source.local.task.OnResultCallback

class SongRepository private constructor(
    private val musicsDataSource: MusicsDataSource
) : MusicsDataSource {

    override fun getData(callback: OnResultCallback) {
        musicsDataSource.getData(callback)
    }

    companion object {
        private var instance: SongRepository? = null
        fun getInstance(musicsDataSource: MusicsDataSource) =
            instance ?: SongRepository(musicsDataSource).also { instance = it }
    }
}
