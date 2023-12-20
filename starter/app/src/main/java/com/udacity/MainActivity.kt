package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.model.DownloadContent
import com.udacity.model.DownloadStatus

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager

    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        notificationManager = ContextCompat.getSystemService(
            applicationContext, NotificationManager::class.java
        ) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        binding.mainContent.customButton.setOnClickListener {
            getUrlAndDownloadSelected()
        }
        createChannel(CHANNEL_ID, "DownloadApp")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            id?.let {
                query.setFilterById(it)
            }
            val cursor = downloadManager.query(query)
            if (cursor?.moveToFirst() == true) {
                val columnTitle = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                val columnStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val title = cursor.getString(columnTitle)
                val status = cursor.getInt(columnStatus)
                val downloadContent = getDownloadContent(title, status)
                sendNotification(downloadContent)
                binding.mainContent.customButton.cancelAnimation()
            }
        }
    }

    private fun sendNotification(content: DownloadContent) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PermissionInfo.PROTECTION_DANGEROUS)
        }else{
            notificationManager.sendNotification(applicationContext, CHANNEL_ID, content)
        }

    }

    private fun getUrlAndDownloadSelected() {
        when (binding.mainContent.selectOption.checkedRadioButtonId) {
            R.id.glide_option -> {
                download(GLIDE_URL, getString(R.string.glide_option))
            }

            R.id.load_app_option -> {
                download(LOADING_APP_URL, getString(R.string.load_app_option))
            }

            R.id.retrofit_option -> {
                download(RETROFIT_URL, getString(R.string.retrofit_option))
            }

            else -> {
                Toast.makeText(
                    applicationContext,
                    getText(R.string.toast_not_select_option),
                    Toast.LENGTH_SHORT
                ).show()
                download()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = "Time for breakfast"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getDownloadContent(title: String, status: Int): DownloadContent {
        var statusReal: DownloadStatus = DownloadStatus.SUCCESS
        if (status == DownloadManager.STATUS_FAILED) {
            statusReal = DownloadStatus.FAILED
        }
        return DownloadContent(title, statusReal)
    }

    private fun download(url: String? = null, title: String? = null) {
        url?.let {
            val request =
                DownloadManager.Request(Uri.parse(url)).setTitle(title)
                    .setDescription(getString(R.string.app_description)).setRequiresCharging(false)
                    .setAllowedOverMetered(true).setAllowedOverRoaming(true)
            downloadID = downloadManager.enqueue(request)
        }
        binding.mainContent.customButton.let {
            it.downloadBegin()
            if(url.isNullOrBlank() && title.isNullOrBlank()){
                it.cancelAnimation()
            }
        }
    }

    companion object {
        private const val LOADING_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }
}