package com.ets.onlinebiblioteka.models

import android.os.Parcelable
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
        var authors: ArrayList<String>,
        var photo: String
    ) : Parcelable
}
