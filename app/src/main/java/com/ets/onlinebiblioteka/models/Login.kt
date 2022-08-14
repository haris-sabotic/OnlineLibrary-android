package com.ets.onlinebiblioteka.models

import com.google.gson.annotations.SerializedName

data class Login(
    val msg: String,

    @SerializedName("plainTextToken")
    val token: String
)
