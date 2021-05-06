@file:Suppress("DEPRECATION")

package com.example.mvpmusicapp.data.source.local.task

import android.os.AsyncTask
import com.example.mvpmusicapp.data.model.Song

class LocalAsyncTask(
    private val functionLambda: () -> List<Song>,
    private val callback: OnResultCallback,
) : AsyncTask<Unit, Unit, List<Song>>() {

    override fun doInBackground(vararg p0: Unit?): List<Song> {
        return functionLambda()
    }

    override fun onPostExecute(result: List<Song>) {
        result.let { if (it.isNotEmpty()) callback.onDataLoaded(it) else callback.onFailed() }
    }
}
