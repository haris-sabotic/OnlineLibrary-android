package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.models.User
import com.ets.onlinebiblioteka.util.GlobalData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {
    companion object {
        val USER_DATA_SHARED_PREFS_KEY = "USER_DATA"
    }

    private val user: MutableLiveData<User?> = MutableLiveData(null)

    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getUser(): LiveData<User?> {
        return user
    }

    fun failure(): LiveData<Boolean> {
        return failure
    }

    fun loadUser() {
        GlobalData.getSharedPreferences().getString(USER_DATA_SHARED_PREFS_KEY, null)?.let {
            val data = Gson().fromJson(it, User::class.java)

            user.postValue(data)
            return
        }

        val token = GlobalData.getToken()

        val api = ApiInterface.create()

        api.getUser("Bearer $token").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        GlobalData.getSharedPreferences().edit().putString(
                            USER_DATA_SHARED_PREFS_KEY,
                            Gson().toJson(it)
                        ).commit()
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