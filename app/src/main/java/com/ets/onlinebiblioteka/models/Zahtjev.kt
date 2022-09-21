package com.ets.onlinebiblioteka.models

import android.os.Parcelable
import com.ets.onlinebiblioteka.models.filters.Autor
import kotlinx.parcelize.Parcelize

@Parcelize
data class Zahtjev(
    var id: String,
    var book: ZahtjevBook,
    var librarian: String,
    var dateFrom: String,
    var dateTo: String,
    var type: String
) : Parcelable {
    @Parcelize
    data class ZahtjevBook(
        var title: String,
        var authors: ArrayList<Autor>,
        var photo: String
    ) : Parcelable
}
