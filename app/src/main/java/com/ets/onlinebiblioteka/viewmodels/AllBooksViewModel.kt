package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.models.Paginated
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllBooksViewModel : ViewModel() {
    private var books: MutableLiveData<Paginated<Book>?> = MutableLiveData(null)

    fun getBooks(): LiveData<Paginated<Book>?> {
        return books
    }

    fun clearBooks() {
        books.postValue(null)
    }

    fun loadBooks(page: Int, textQuery: String?, filters: SelectedFilters?) {
        val call: Call<Paginated<Book>>

        // create api call with page, search query and selected filters
        if (filters == null) {
            call = ApiInterface.create().searchBooks(
                page,
                textQuery,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        } else {
            call = ApiInterface.create().searchBooks(
                page,
                textQuery,
                // transform chip names to identifiers used by the api
                when (filters.availability) {
                    "Izdato" -> "rented"
                    "Rezervisano" -> "reserved"
                    "Na raspolaganju" -> "available"
                    else -> null
                },
                // get only IDs from every (ID, name) pair
                filters.categories.map { it.first },
                filters.genres.map { it.first },
                filters.authors.map { it.first },
                filters.publisher?.first,
                filters.script?.first,
                filters.language?.first
            )
        }

        call.enqueue(object : Callback<Paginated<Book>> {
            override fun onResponse(
                call: Call<Paginated<Book>>,
                response: Response<Paginated<Book>>
            ) {
                response.body()?.let {
                    books.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Book>>, t: Throwable) { }
        })
    }
}