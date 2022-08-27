package com.ets.onlinebiblioteka.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    var title: String,
    var authors: ArrayList<String>,
    var photo: String
) : Parcelable
