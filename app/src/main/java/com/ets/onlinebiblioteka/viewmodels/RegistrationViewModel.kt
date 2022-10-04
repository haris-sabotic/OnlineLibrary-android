package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Registration
import com.ets.onlinebiblioteka.util.ApiInterface
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationViewModel : ViewModel() {
    enum class RegistrationStatus {
        PASSWORD_TOO_SHORT,
        PASSWORD_CONFIRMATION_INVALID,
        INVALID_JMBG,
        EMPTY_FIELDS,
        JMBG_TAKEN,
        EMAIL_TAKEN,
        USERNAME_TAKEN,
        FAILURE,
        SUCCESS
    }

    var token: String? = null

    private val status: MutableLiveData<RegistrationStatus?> = MutableLiveData(null)

    fun getStatus(): LiveData<RegistrationStatus?> {
        return status
    }

    fun register(name: String, email: String, username: String, jmbg: String, pass: String, passAgain: String) {
        // validation
        if (pass != passAgain) {
            status.postValue(RegistrationStatus.PASSWORD_CONFIRMATION_INVALID)
            return
        }

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || jmbg.isEmpty() || pass.isEmpty()) {
            status.postValue(RegistrationStatus.EMPTY_FIELDS)
            return
        }

        if (jmbg.length != 13) {
            status.postValue(RegistrationStatus.INVALID_JMBG)
            return
        }

        if (pass.length < 8) {
            status.postValue(RegistrationStatus.PASSWORD_TOO_SHORT)
            return
        }

        val api = ApiInterface.create()
        api.register(username, name, email, jmbg, pass).enqueue(object : Callback<Registration> {
            override fun onResponse(call: Call<Registration>, response: Response<Registration>) {
                if (response.isSuccessful) {
                    token = response.body()!!.token
                    status.postValue(RegistrationStatus.SUCCESS)
                } else {
                    val r = Gson().fromJson(
                        response.errorBody()!!.string(),
                        Registration::class.java
                    ) as Registration

                    status.postValue(
                        when (r.msg) {
                            "username" -> RegistrationStatus.USERNAME_TAKEN
                            "email" -> RegistrationStatus.EMAIL_TAKEN
                            "jmbg" -> RegistrationStatus.JMBG_TAKEN
                            else -> RegistrationStatus.FAILURE
                        }
                    )
                }
            }

            override fun onFailure(call: Call<Registration>, t: Throwable) {
                status.postValue(RegistrationStatus.FAILURE)
            }
        })
    }
}