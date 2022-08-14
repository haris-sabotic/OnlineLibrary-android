package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.ForgotLogin
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotLoginViewModel : ViewModel() {
    private val msg: MutableLiveData<String?> = MutableLiveData(null)

    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun failure(): LiveData<Boolean> {
        return failure
    }

    fun msg(): LiveData<String?> {
        return msg
    }


    fun forgotPassword(username: String) {
        ApiInterface.create().forgotPassword(username).enqueue(object : Callback<ForgotLogin> {
            override fun onResponse(call: Call<ForgotLogin>, response: Response<ForgotLogin>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        msg.postValue(it.msg)
                    }
                }
            }

            override fun onFailure(call: Call<ForgotLogin>, t: Throwable) {
                failure.postValue(true)
            }
        })
    }

    fun forgotUsername(email: String) {
        ApiInterface.create().forgotUsername(email).enqueue(object : Callback<ForgotLogin> {
            override fun onResponse(call: Call<ForgotLogin>, response: Response<ForgotLogin>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        msg.postValue(it.msg)
                    }
                }
            }

            override fun onFailure(call: Call<ForgotLogin>, t: Throwable) {
                failure.postValue(true)
            }
        })
    }
}