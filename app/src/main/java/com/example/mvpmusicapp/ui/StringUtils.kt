package com.example.mvpmusicapp.ui

import android.content.Context
import com.example.mvpmusicapp.R

fun convertToDurationFormat(context: Context, duration: Double?): String {
    val durationStr = String.format(
        context.getString(R.string.text_format_duration),
        (duration?.div(60000))
    ).replace(
        if (duration.toString().contains(context.getString(R.string.text_comma))) context.getString(
            R.string.text_comma
        )
        else context.getString(R.string.text_dot), context.getString(R.string.text_colon)
    )

    val partOfTime = durationStr.split(context.getString(R.string.text_colon))
    val durationBuilder =
        StringBuilder(partOfTime[0]).append(context.getString(R.string.text_colon))
    if (partOfTime.size > 1) {
        if (((partOfTime[1].toDouble() / 100) * 60).toInt() < 10) durationBuilder.append(
            context.getString(
                R.string.number_zero
            )
        )
        durationBuilder.append(((partOfTime[1].toDouble() / 100) * 60).toInt())
    }
    return durationBuilder.toString()
}
