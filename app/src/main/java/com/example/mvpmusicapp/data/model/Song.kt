package com.example.mvpmusicapp.data.model

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

class Song(var uri: Uri, var name: String, var artist: String, var duration: Int) {

    var isSelected = false

    constructor(cursor: Cursor) : this(
        ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toLong()
        ),
        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST)),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
        } else {
            0
        }
    )

}
