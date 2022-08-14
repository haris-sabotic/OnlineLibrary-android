package com.ets.onlinebiblioteka.util

import com.ets.onlinebiblioteka.models.Login
import com.ets.onlinebiblioteka.models.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    companion object {
        private val BASE_URL = "https://tim7.ictcortex.me/api/"

        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }

    @GET("user")
    fun getUser(@Header("Authorization") token: String): Call<User>

    @POST("login")
    fun login(@Query("username") username: String, @Query("password") password: String): Call<Login>
}