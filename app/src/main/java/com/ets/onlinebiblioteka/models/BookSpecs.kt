package com.ets.onlinebiblioteka.models

import com.google.gson.annotations.SerializedName

data class BookSpecs(
    val pages: String,

    @SerializedName("pismo")
    val script: String,

    @SerializedName("jezik")
    val language: String,

    @SerializedName("povez")
    val binding: String,

    val format: String,
    val isbn: String
)
