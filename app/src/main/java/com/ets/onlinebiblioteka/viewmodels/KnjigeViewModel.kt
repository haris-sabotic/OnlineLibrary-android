package com.ets.onlinebiblioteka.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.models.Paginated
import com.ets.onlinebiblioteka.models.filters.Kategorija
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.models.filters.Zanr
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KnjigeViewModel : ViewModel() {
    private var books: MutableLiveData<Paginated<Book>?> = MutableLiveData(null)
    private var categories: MutableLiveData<Paginated<Kategorija>?> = MutableLiveData(null)
    private var genres: MutableLiveData<Paginated<Zanr>?> = MutableLiveData(null)
    private var failure: MutableLiveData<Boolean> = MutableLiveData(false)
    private var selectedFilters: MutableLiveData<SelectedFilters?> = MutableLiveData(null)

    fun getBooks(): LiveData<Paginated<Book>?> {
        return books
    }

    fun getCategories(): LiveData<Paginated<Kategorija>?> {
        return categories
    }

    fun getGenres(): LiveData<Paginated<Zanr>?> {
        return genres
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

    fun loadCategories(page: Int = 1) {
        ApiInterface.create().getKategorije(page).enqueue(object : Callback<Paginated<Kategorija>> {
            override fun onResponse(
                call: Call<Paginated<Kategorija>>,
                response: Response<Paginated<Kategorija>>
            ) {
                response.body()?.let {
                    categories.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Kategorija>>, t: Throwable) { }
        })
    }

    fun loadGenres(page: Int = 1) {
        ApiInterface.create().getZanrovi(page).enqueue(object : Callback<Paginated<Zanr>> {
            override fun onResponse(
                call: Call<Paginated<Zanr>>,
                response: Response<Paginated<Zanr>>
            ) {
                response.body()?.let {
                    genres.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Zanr>>, t: Throwable) { }
        })
    }

    fun search(textQuery: String?) {
        val call: Call<Paginated<Book>>
        val filters = selectedFilters.value

        // create api call with page, search query and selected filters
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

            override fun onFailure(call: Call<Paginated<Book>>, t: Throwable) {
                failure.postValue(true)
            }
        })
    }
}