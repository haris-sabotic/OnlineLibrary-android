package com.ets.onlinebiblioteka.util

import com.ets.onlinebiblioteka.models.*
import com.ets.onlinebiblioteka.models.filters.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

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

    @POST("forgot-password")
    fun forgotPassword(@Query("username") username: String): Call<ForgotLogin>
    @POST("forgot-username")
    fun forgotUsername(@Query("email") email: String): Call<ForgotLogin>

    @POST("edit-user")
    fun editUser(@Header("Authorization") token: String,
                 @Query("name") name: String, @Query("email") email: String,
                 @Query("username") username: String, @Query("oldPass") oldPass: String,
                 @Query("newPass") newPass: String): Call<EditUser>

    @GET("zahtjevi")
    fun mojiZahtjevi(@Header("Authorization") token: String,
                     @Query("filter") filter: String): Call<ArrayList<Zahtjev>>

    @GET("aktivnosti")
    fun aktivnosti(@Header("Authorization") token: String): Call<ArrayList<Aktivnost>>

    @DELETE("izbrisi-transakciju")
    fun izbrisiTransakciju(@Header("Authorization") token: String,
                           @Query("id") id: String,
                           @Query("type") type: String): Call<IzbrisiTransakciju>


    @GET("kategorije")
    fun getKategorije(@Query("page") page: Int): Call<Paginated<Kategorija>>

    @GET("zanrovi")
    fun getZanrovi(@Query("page") page: Int): Call<Paginated<Zanr>>

    @GET("autori")
    fun getAutori(@Query("page") page: Int): Call<Paginated<Autor>>

    @GET("izdavaci")
    fun getIzdavaci(@Query("page") page: Int): Call<Paginated<Izdavac>>

    @GET("pisma")
    fun getPisma(@Query("page") page: Int): Call<Paginated<Pismo>>

    @GET("jezici")
    fun getJezici(@Query("page") page: Int): Call<Paginated<Jezik>>

    @GET("search-books")
    fun searchBooks(@Query("page") page: Int?,
                    @Query("query") query: String?,
                    @Query("availability") availability: String?,
                    @Query("categories[]") categoryIds: List<Int>?,
                    @Query("genres[]") genreIds: List<Int>?,
                    @Query("authors[]") authorIds: List<Int>?,
                    @Query("publisher") publisherId: Int?,
                    @Query("script") scriptId: Int?,
                    @Query("language") languageId: Int?): Call<Paginated<Book>>

    @GET("book-spec/{book}")
    fun bookSpecs(@Path("book") bookId: Int): Call<BookSpecs>

    @GET("similar-books/{book}")
    fun getSimilarBooks(@Path("book") bookId: Int): Call<ArrayList<Book>>
}