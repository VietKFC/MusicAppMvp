package com.example.mvpmusicapp.ui

import com.example.mvpmusicapp.data.model.Song

interface SongInterface {
    interface Presenter {
        fun getLocalSongs()
        fun playMusic(status: Boolean)
    }

    interface View {
        fun getAllSongList(songList: List<Song>?)
        fun showErrorMessage()
        fun setPlayButton()
        fun setPauseButton()
    }
}
