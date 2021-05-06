package com.example.mvpmusicapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvpmusicapp.R
import com.example.mvpmusicapp.data.model.Song
import com.example.mvpmusicapp.databinding.SongItemBinding
import com.example.mvpmusicapp.ui.convertToDurationFormat

class SongAdapter(private val clickItem: ((Int) -> Unit)) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private var songs = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val songBinding =
            SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(songBinding.root, songBinding, parent.context, clickItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bindView(songs[position])

    override fun getItemCount() = songs.size


    fun setSongList(songList: List<Song>) {
        this.songs = songList as MutableList<Song>
        notifyDataSetChanged()
    }

    class ViewHolder(
        itemView: View,
        private val binding: SongItemBinding,
        private val context: Context,
        private val clickItem: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            binding.constraintSongItem.setOnClickListener {
                clickItem(adapterPosition)
            }
        }

        fun bindView(song: Song) {
            binding.apply {
                textName.text = song.name
                textInfor.text = context.resources.getString(
                    R.string.text_music_infor,
                    convertToDurationFormat(context, song.duration.toDouble()),
                    song.artist
                )
            }
        }
    }
}
