package com.ets.onlinebiblioteka.models.filters

import com.ets.onlinebiblioteka.util.FilterModelController

data class Autor(
    val id: Int,

    val name: String,
    val biography: String,
) : FilterModelController {
    override fun getChipText(): String {
        return name
    }

    override fun getChipId(): Int {
        return id
    }
}
