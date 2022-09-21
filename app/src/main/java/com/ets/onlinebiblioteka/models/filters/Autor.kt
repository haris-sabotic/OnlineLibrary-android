package com.ets.onlinebiblioteka.models.filters

import android.os.Parcelable
import com.ets.onlinebiblioteka.util.FilterModelController
import kotlinx.parcelize.Parcelize

@Parcelize
data class Autor(
    val id: Int,

    val name: String,
    val biography: String?,
) : Parcelable, FilterModelController {
    override fun getChipText(): String {
        return name
    }

    override fun getChipId(): Int {
        return id
    }
}
