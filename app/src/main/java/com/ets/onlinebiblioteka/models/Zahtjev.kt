package com.ets.onlinebiblioteka.models

data class Zahtjev(
    var id: String,
    var book: Book,
    var librarian: String,
    var dateFrom: String,
    var dateTo: String,
    var type: String
)
