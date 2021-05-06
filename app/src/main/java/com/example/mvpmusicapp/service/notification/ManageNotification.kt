@file:Suppress("DEPRECATION")

package com.example.mvpmusicapp.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.mvpmusicapp.R
import com.example.mvpmusicapp.data.model.Song

private const val INTENT_REQUEST_CODE = 100
private const val FLAGS = 0

fun createNotificationChannel(context: Context, notificationManager: NotificationManager?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            context.getString(R.string.infor_channel_id),
            context.getString(R.string.infor_channel_name), NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(notificationChannel)
    }
}

fun sendNotification(song: Song?, context: Context, isPlaying: Boolean?): Notification {
    val playIntent = Intent(context, MusicReceiver::class.java)
    playIntent.action = context.getString(R.string.intent_play_music)
    val playPendingIntent =
        PendingIntent.getBroadcast(context, INTENT_REQUEST_CODE, playIntent, FLAGS)

    val nextIntent = Intent(context, MusicReceiver::class.java)
    nextIntent.action = context.getString(R.string.intent_next_music)
    val nextPendingIntent =
        PendingIntent.getBroadcast(context, INTENT_REQUEST_CODE, nextIntent, FLAGS)

    val prevIntent = Intent(context, MusicReceiver::class.java)
    prevIntent.action = context.getString(R.string.intent_prev_music)
    val prevPendingIntent =
        PendingIntent.getBroadcast(context, INTENT_REQUEST_CODE, prevIntent, FLAGS)

    val contentView = RemoteViews(context.packageName, R.layout.notification_custom)
    contentView.apply {
        setImageViewResource(R.id.imageNotifyMusic, R.drawable.ic_music)
        setImageViewResource(
            R.id.imagePlayMusic,
            if (isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_button
        )
        setImageViewResource(R.id.imageForward, R.drawable.ic_forward_button)
        setImageViewResource(R.id.imageRewind, R.drawable.ic_rewind_button)
        setTextViewText(R.id.textNotifyName, song?.name)
        setTextViewText(R.id.text_notify_infor, song?.artist)
        setOnClickPendingIntent(R.id.imagePlayMusic, playPendingIntent)
        setOnClickPendingIntent(R.id.imageForward, nextPendingIntent)
        setOnClickPendingIntent(R.id.imageRewind, prevPendingIntent)
    }
    return NotificationCompat.Builder(context, context.getString(R.string.infor_channel_id))
        .setSmallIcon(R.drawable.ic_music)
        .setContentTitle(context.getString(R.string.app_name))
        .setCustomBigContentView(contentView)
        .setAutoCancel(true)
        .build()
}
