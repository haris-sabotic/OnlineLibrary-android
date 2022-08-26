package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Login
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val loginResponse: MutableLiveData<Login?> = MutableLiveData(null)

    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun failure(): LiveData<Boolean> {
        return failure
    }

    fun loginResponse(): LiveData<Login?> {
        return loginResponse
    }

    fun login(username: String, password: String) {
        ApiInterface.create().login(username, password).enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        loginResponse.postValue(it)
                    }
                } else {
                    loginResponse.postValue(Login("failure", ""))
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                failure.postValue(true)
            }
        })
    }
}