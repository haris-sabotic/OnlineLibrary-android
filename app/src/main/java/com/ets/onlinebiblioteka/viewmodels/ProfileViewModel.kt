package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.models.User
import com.ets.onlinebiblioteka.util.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {
    private val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            loadUser()
        }
    }

    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getUser(): LiveData<User> {
        return user
    }

    fun failure(): LiveData<Boolean> {
        return failure
    }

    private fun loadUser() {
        val token = GlobalData.getToken()

        val api = ApiInterface.create()

        api.getUser("Bearer $token").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        user.postValue(it)
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                failure.postValue(true)
            }
        })
    }
}