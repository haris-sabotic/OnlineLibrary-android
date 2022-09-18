package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.BookSpecs
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookDetailsViewModel : ViewModel() {
    private var specs: MutableLiveData<BookSpecs?> = MutableLiveData(null)

    fun getSpecs(): LiveData<BookSpecs?> {
        return specs
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
}