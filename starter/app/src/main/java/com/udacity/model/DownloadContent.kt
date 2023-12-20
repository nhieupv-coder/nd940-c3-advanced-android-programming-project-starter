package com.udacity.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class DownloadContent(
    val title: String,
    val status: DownloadStatus
) : Parcelable