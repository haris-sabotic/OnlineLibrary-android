package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Aktivnost
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.util.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AktivnostiViewModel : ViewModel() {
    private val items: MutableLiveData<ArrayList<Aktivnost>> = MutableLiveData(arrayListOf())

    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun failure(): LiveData<Boolean> {
        return failure
    }

    fun getItems(): LiveData<ArrayList<Aktivnost>> {
        return items
    }

    fun loadItems() {
        ApiInterface.create()
            .aktivnosti("Bearer ${GlobalData.getToken()}")
            .enqueue(object : Callback<ArrayList<Aktivnost>> {
                override fun onResponse(
                    call: Call<ArrayList<Aktivnost>>,
                    response: Response<ArrayList<Aktivnost>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            items.postValue(it)
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<Aktivnost>>, t: Throwable) {
                    failure.postValue(true)
                }
            })
    }
}