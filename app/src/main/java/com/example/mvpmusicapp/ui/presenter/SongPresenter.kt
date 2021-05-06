package com.example.mvpmusicapp.ui.presenter

import com.example.mvpmusicapp.data.model.Song
import com.example.mvpmusicapp.data.repository.SongRepository
import com.example.mvpmusicapp.data.source.local.task.OnResultCallback
import com.example.mvpmusicapp.ui.SongInterface

class SongPresenter(
    private val songRepository: SongRepository,
    private val view: SongInterface.View
) : SongInterface.Presenter {

    override fun getLocalSongs() {
        songRepository.getData(object : OnResultCallback {
            override fun onDataLoaded(data: List<Song>?) {
                view.getAllSongList(data)
            }

            override fun onFailed() {
                view.showErrorMessage()
            }
        })
    }

    override fun playMusic(status: Boolean) {
        if (status) view.setPlayButton() else view.setPauseButton()
    }
}
