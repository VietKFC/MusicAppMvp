package com.example.mvpmusicapp.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mvpmusicapp.R

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(
            Intent(context.getString(R.string.intent_action)).putExtra(
                context.getString(R.string.intent_data_extra),
                intent?.action
            )
        )
    }
}
