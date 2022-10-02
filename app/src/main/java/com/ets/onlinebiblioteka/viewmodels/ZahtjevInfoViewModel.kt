package com.ets.onlinebiblioteka.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.IzbrisiTransakciju
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.util.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ZahtjevInfoViewModel : ViewModel() {
    private val msg: MutableLiveData<String?> = MutableLiveData(null)

    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun failure(): LiveData<Boolean> {
        return failure
    }

    fun getMsg(): LiveData<String?> {
        return msg
    }

    fun izbrisi(id: String, type: String) {
        ApiInterface.create()
            .izbrisiTransakciju("Bearer ${GlobalData.getToken()}", id, type)
            .enqueue(object : Callback<IzbrisiTransakciju> {
                override fun onResponse(
                    call: Call<IzbrisiTransakciju>,
                    response: Response<IzbrisiTransakciju>
                ) {
                    response.body()?.let {
                        msg.postValue(it.msg)
                    }
                }

                override fun onFailure(call: Call<IzbrisiTransakciju>, t: Throwable) {
                    failure.postValue(true)
                }
            })
    }
}