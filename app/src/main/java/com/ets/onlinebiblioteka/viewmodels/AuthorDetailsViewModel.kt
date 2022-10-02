package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.models.Paginated
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthorDetailsViewModel : ViewModel() {
    private var moreBooks: MutableLiveData<List<Book>> = MutableLiveData(listOf())

    fun getMoreBooks(): LiveData<List<Book>> {
        return moreBooks
    }

    fun loadMoreBooks(authorId: Int, page: Int = 0) {
        ApiInterface.create().searchBooks(
            page,
            null,
            null,
            null,
            null,
            listOf(authorId),
            null,
            null,
            null
        ).enqueue(object : Callback<Paginated<Book>> {
            override fun onResponse(call: Call<Paginated<Book>>, response: Response<Paginated<Book>>) {
                response.body()?.let {
                    moreBooks.postValue(it.data)
                }
            }

            override fun onFailure(call: Call<Paginated<Book>>, t: Throwable) {}
        })
    }
}