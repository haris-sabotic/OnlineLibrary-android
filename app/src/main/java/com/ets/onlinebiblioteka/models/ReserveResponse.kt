package com.ets.onlinebiblioteka.models

import com.google.gson.annotations.SerializedName

data class ReserveResponse(
    @SerializedName("msg")
    val msg: String
)
