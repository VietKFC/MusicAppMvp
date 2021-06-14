package com.example.mvpmusicapp.data.source

import com.example.mvpmusicapp.data.source.local.task.OnResultCallback

interface MusicsDataSource {
    fun getData(callback: OnResultCallback)
}
