package com.ets.onlinebiblioteka.models.filters

import com.ets.onlinebiblioteka.util.FilterModelController

data class Kategorija(
    val id: Int,

    val name: String,
    val photo: String,
    val description: String,
) : FilterModelController {
    override fun getChipText(): String {
        return name
    }

    override fun getChipId(): Int {
        return id
    }
}
