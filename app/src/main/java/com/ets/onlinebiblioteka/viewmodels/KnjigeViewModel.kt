package com.ets.onlinebiblioteka.viewmodels

import android.util.Log
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

class KnjigeViewModel : ViewModel() {
    private var books: MutableLiveData<Paginated<Book>?> = MutableLiveData(null)
    private var failure: MutableLiveData<Boolean> = MutableLiveData(false)
    private var selectedFilters: MutableLiveData<SelectedFilters?> = MutableLiveData(null)

    fun getBooks(): LiveData<Paginated<Book>?> {
        return books
    }

    fun getSearchFailure(): LiveData<Boolean> {
        return failure
    }

    fun getSelectedFilters(): LiveData<SelectedFilters?> {
        return selectedFilters
    }
    fun setSelectedFilters(_selectedFilters: SelectedFilters?) {
        selectedFilters.postValue(_selectedFilters)
    }


    fun search(textQuery: String?) {
        val call: Call<Paginated<Book>>
        val filters = selectedFilters.value

        if (filters == null) {
            call = ApiInterface.create().searchBooks(
                1,
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
                1,
                textQuery,
                when (filters.availability) {
                    "Izdato" -> "rented"
                    "Rezervisano" -> "reserved"
                    "Na raspolaganju" -> "available"
                    else -> null
                },
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

            override fun onFailure(call: Call<Paginated<Book>>, t: Throwable) {
                failure.postValue(true)
            }
        })
    }
}