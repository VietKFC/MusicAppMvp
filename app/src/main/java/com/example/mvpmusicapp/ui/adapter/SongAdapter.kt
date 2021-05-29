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

class SongAdapter(
    private val clickItem: ((Int) -> Unit),
    private val longClickItem: (Song) -> Unit
) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private var songs = mutableListOf<Song>()
    private var isCheckboxHide = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val songBinding =
            SongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            songBinding.root,
            songBinding,
            parent.context,
            clickItem,
            longClickItem
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bindView(songs[position] , isCheckboxHide)

    override fun getItemCount() = songs.size


    fun setSongList(songList: List<Song>) {
        this.songs = songList as MutableList<Song>
        notifyDataSetChanged()
    }

    fun setCheckboxHide(isCheckboxShow: Boolean) {
        isCheckboxHide = isCheckboxShow
        notifyDataSetChanged()
    }

    class ViewHolder(
        itemView: View,
        private val binding: SongItemBinding,
        private val context: Context,
        private val clickItem: (Int) -> Unit,
        private val longClickItem: (Song) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private var song: Song? = null

        init {
            binding.apply {
                constraintSongItem.setOnClickListener {
                    if (checkboxSong.visibility != View.VISIBLE) {
                        clickItem(adapterPosition)
                    } else {
                        checkboxSong.apply {
                            isChecked = !isChecked
                            song?.isSelected = isChecked
                            song?.let { it1 -> longClickItem(it1) }
                        }
                    }
                }

                constraintSongItem.setOnLongClickListener {
                    checkboxSong.visibility = View.VISIBLE
                    song?.isSelected = checkboxSong.isChecked
                    song?.let { it1 -> longClickItem(it1) }
                    false
                }

                checkboxSong.setOnClickListener {
                    song?.isSelected = checkboxSong.isChecked
                    song?.let { it1 -> longClickItem(it1) }
                }

            }

        }

        fun bindView(song: Song , isCheckboxHide: Boolean) {
            this.song = song
            binding.apply {
                if(isCheckboxHide) {
                    checkboxSong.visibility = View.GONE
                }
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
