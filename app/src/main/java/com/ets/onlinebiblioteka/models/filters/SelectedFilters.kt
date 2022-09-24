package com.ets.onlinebiblioteka.models.filters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectedFilters(
    var availability: String?,

    var categories: MutableList<Pair<Int, String>>,
    var genres: MutableList<Pair<Int, String>>,
    val authors: MutableList<Pair<Int, String>>,

    var publisher: Pair<Int, String>?,
    var script: Pair<Int, String>?,
    var language: Pair<Int, String>?,

    ) : Parcelable {
    fun isEmpty(): Boolean {
        return (categories.isEmpty() &&
                genres.isEmpty() &&
                authors.isEmpty() &&
                (availability == null) &&
                (publisher == null) &&
                (script == null) &&
                (language == null))
    }
}
