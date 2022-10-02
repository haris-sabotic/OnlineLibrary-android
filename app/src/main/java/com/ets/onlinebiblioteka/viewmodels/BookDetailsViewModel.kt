package com.ets.onlinebiblioteka.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.models.BookSpecs
import com.ets.onlinebiblioteka.models.ReserveResponse
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.util.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookDetailsViewModel : ViewModel() {
    private var specs: MutableLiveData<BookSpecs?> = MutableLiveData(null)
    private var similarBooks: MutableLiveData<ArrayList<Book>> = MutableLiveData(arrayListOf())
    private var reserveResponse: MutableLiveData<ReserveResponse?> = MutableLiveData(null)

    fun getSpecs(): LiveData<BookSpecs?> {
        return specs
    }

    fun getSimilarBooks(): LiveData<ArrayList<Book>> {
        return similarBooks
    }

    fun getReserveResponse(): LiveData<ReserveResponse?> {
        return reserveResponse
    }

    fun reserveBook(bookId: Int, dateFrom: String, dateTo: String, phoneNumber: String) {
        ApiInterface.create().reserve("Bearer ${GlobalData.getToken()!!}", bookId, dateFrom, dateTo, phoneNumber).enqueue(object : Callback<ReserveResponse> {
            override fun onResponse(
                call: Call<ReserveResponse>,
                response: Response<ReserveResponse>
            ) {
                response.body()?.let {
                    reserveResponse.postValue(it)
                }
            }

            override fun onFailure(call: Call<ReserveResponse>, t: Throwable) { }
        })
    }

    fun loadSpecs(bookId: Int) {
        ApiInterface.create().bookSpecs(bookId).enqueue(object : Callback<BookSpecs> {
            override fun onResponse(call: Call<BookSpecs>, response: Response<BookSpecs>) {
                response.body()?.let {
                    specs.postValue(it)
                }
            }

            override fun onFailure(call: Call<BookSpecs>, t: Throwable) {
            }
        })
    }

    fun loadSimilarBooks(bookId: Int) {
        ApiInterface.create().getSimilarBooks(bookId).enqueue(object : Callback<ArrayList<Book>> {
            override fun onResponse(call: Call<ArrayList<Book>>, response: Response<ArrayList<Book>>) {
                response.body()?.let {
                    similarBooks.postValue(it)
                }
            }

            override fun onFailure(call: Call<ArrayList<Book>>, t: Throwable) {
            }
        })
    }
}