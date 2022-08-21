package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.adapters.MojiZahtjeviAdapter
import com.ets.onlinebiblioteka.models.Zahtjev
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.util.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MojiZahtjeviViewModel : ViewModel() {
    private val items: MutableLiveData<ArrayList<Zahtjev>> = MutableLiveData(arrayListOf())

    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun failure(): LiveData<Boolean> {
        return failure
    }

    fun getItems(): LiveData<ArrayList<Zahtjev>> {
        return items
    }

    fun loadItems(filter: String) {
        ApiInterface.create()
            .mojiZahtjevi("Bearer ${GlobalData.getToken()}", filter)
            .enqueue(object : Callback<ArrayList<Zahtjev>> {
                override fun onResponse(
                    call: Call<ArrayList<Zahtjev>>,
                    response: Response<ArrayList<Zahtjev>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            items.postValue(it)
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<Zahtjev>>, t: Throwable) {
                    failure.postValue(true)
                }
            })
    }
}