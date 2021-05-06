package com.example.mvpmusicapp.data.source.local.task

import com.example.mvpmusicapp.data.model.Song

interface OnResultCallback {
    fun onDataLoaded(data: List<Song>?)
    fun onFailed()
}
