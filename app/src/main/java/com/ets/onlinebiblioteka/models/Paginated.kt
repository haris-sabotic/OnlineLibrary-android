package com.ets.onlinebiblioteka.models

import com.google.gson.annotations.SerializedName

data class Paginated<T>(
    @SerializedName("current_page")
    val currentPage: Int,

    val data: List<T>,


    @SerializedName("next_page_url")
    val nextPageUrl: String?,

    @SerializedName("prev_page_url")
    val prevPageUrl: String?,

    @SerializedName("to")
    val count: String?,
)
