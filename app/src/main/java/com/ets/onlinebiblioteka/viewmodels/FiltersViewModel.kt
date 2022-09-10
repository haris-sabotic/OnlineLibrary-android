package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Paginated
import com.ets.onlinebiblioteka.models.filters.*
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FiltersViewModel : ViewModel() {
    private var kategorije: MutableLiveData<Paginated<Kategorija>> = MutableLiveData()
    private var zanrovi: MutableLiveData<Paginated<Zanr>> = MutableLiveData()
    private var autori: MutableLiveData<Paginated<Autor>> = MutableLiveData()
    private var izdavaci: MutableLiveData<Paginated<Izdavac>> = MutableLiveData()
    private var pisma: MutableLiveData<Paginated<Pismo>> = MutableLiveData()
    private var jezici: MutableLiveData<Paginated<Jezik>> = MutableLiveData()

    fun getKategorije(): LiveData<Paginated<Kategorija>> {
        return kategorije
    }

    fun getZanrovi(): LiveData<Paginated<Zanr>> {
        return zanrovi
    }

    fun getAutori(): LiveData<Paginated<Autor>> {
        return autori
    }

    fun getIzdavaci(): LiveData<Paginated<Izdavac>> {
        return izdavaci
    }

    fun getPisma(): LiveData<Paginated<Pismo>> {
        return pisma
    }

    fun getJezici(): LiveData<Paginated<Jezik>> {
        return jezici
    }

    fun loadKategorije(page: Int = 1) {
        ApiInterface.create().getKategorije(page).enqueue(object : Callback<Paginated<Kategorija>> {
            override fun onResponse(
                call: Call<Paginated<Kategorija>>,
                response: Response<Paginated<Kategorija>>
            ) {
                response.body()?.let {
                    kategorije.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Kategorija>>, t: Throwable) {
            }
        })
    }

    fun loadZanrovi(page: Int = 1) {
        ApiInterface.create().getZanrovi(page).enqueue(object : Callback<Paginated<Zanr>> {
            override fun onResponse(
                call: Call<Paginated<Zanr>>,
                response: Response<Paginated<Zanr>>
            ) {
                response.body()?.let {
                    zanrovi.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Zanr>>, t: Throwable) {
            }
        })
    }


    fun loadAutori(page: Int = 1) {
        ApiInterface.create().getAutori(page).enqueue(object : Callback<Paginated<Autor>> {
            override fun onResponse(
                call: Call<Paginated<Autor>>,
                response: Response<Paginated<Autor>>
            ) {
                response.body()?.let {
                    autori.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Autor>>, t: Throwable) {
            }
        })
    }

    fun loadIzdavaci(page: Int = 1) {
        ApiInterface.create().getIzdavaci(page).enqueue(object : Callback<Paginated<Izdavac>> {
            override fun onResponse(
                call: Call<Paginated<Izdavac>>,
                response: Response<Paginated<Izdavac>>
            ) {
                response.body()?.let {
                    izdavaci.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Izdavac>>, t: Throwable) {
            }
        })
    }

    fun loadPisma(page: Int = 1) {
        ApiInterface.create().getPisma(page).enqueue(object : Callback<Paginated<Pismo>> {
            override fun onResponse(
                call: Call<Paginated<Pismo>>,
                response: Response<Paginated<Pismo>>
            ) {
                response.body()?.let {
                    pisma.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Pismo>>, t: Throwable) {
            }
        })
    }

    fun loadJezici(page: Int = 1) {
        ApiInterface.create().getJezici(page).enqueue(object : Callback<Paginated<Jezik>> {
            override fun onResponse(
                call: Call<Paginated<Jezik>>,
                response: Response<Paginated<Jezik>>
            ) {
                response.body()?.let {
                    jezici.postValue(it)
                }
            }

            override fun onFailure(call: Call<Paginated<Jezik>>, t: Throwable) {
            }
        })
    }
}