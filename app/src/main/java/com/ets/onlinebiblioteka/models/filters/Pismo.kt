package com.ets.onlinebiblioteka.models.filters

import com.ets.onlinebiblioteka.util.FilterModelController

data class Pismo(
    val id: Int,
    val name: String,
) : FilterModelController {
    override fun getChipText(): String {
        return name
    }

    override fun getChipId(): Int {
        return id
    }
}
