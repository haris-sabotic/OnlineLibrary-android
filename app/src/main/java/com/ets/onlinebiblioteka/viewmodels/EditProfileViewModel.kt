package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.EditUser
import com.ets.onlinebiblioteka.util.ApiInterface
import com.ets.onlinebiblioteka.util.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileViewModel : ViewModel() {
    enum class EditProfileStatus {
        OLD_PASSWORD_INVALID,
        PASSWORD_CONFIRMATION_INVALID,
        EMPTY_FIELDS,
        SUCCESS
    }

    private val status: MutableLiveData<EditProfileStatus?> = MutableLiveData(null)
    private val failure: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getStatus(): LiveData<EditProfileStatus?> {
        return status
    }

    fun failure(): LiveData<Boolean> {
        return failure
    }

    fun editUser(name: String, email: String, username: String, oldPass: String, newPass: String, newPassAgain: String) {
        if (newPass != newPassAgain) {
            status.postValue(EditProfileStatus.PASSWORD_CONFIRMATION_INVALID)
            return
        }

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()) {
            status.postValue(EditProfileStatus.EMPTY_FIELDS)
            return
        }


        val token = GlobalData.getToken()
        val api = ApiInterface.create()
        api.editUser("Bearer $token", name, email, username, oldPass, newPass).enqueue(object : Callback<EditUser> {
            override fun onResponse(call: Call<EditUser>, response: Response<EditUser>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.msg == "old password invalid") {
                            status.postValue(EditProfileStatus.OLD_PASSWORD_INVALID)
                        } else {
                            status.postValue(EditProfileStatus.SUCCESS)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<EditUser>, t: Throwable) {
                failure.postValue(true)
            }

        })
    }
}