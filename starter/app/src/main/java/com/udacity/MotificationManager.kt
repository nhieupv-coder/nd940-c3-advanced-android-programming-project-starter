package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.model.DownloadContent


const val DATA_INFO_CONTENT = "data_content"
const val NOTIFICATION_ID = 0
fun NotificationManager.sendNotification(
    applicationContext: Context, channelId: String, content: DownloadContent
) {
    val contentIntent =
        Intent(applicationContext, DetailActivity::class.java).putExtra(DATA_INFO_CONTENT, content)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_IMMUTABLE or
                PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(
        applicationContext, channelId
    ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_title),
            contentPendingIntent
        )
        .setAutoCancel(true)
        .build()
    notify(NOTIFICATION_ID, builder)
}

