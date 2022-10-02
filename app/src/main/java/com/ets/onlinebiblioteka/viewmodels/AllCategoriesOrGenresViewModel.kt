package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.Book
import com.ets.onlinebiblioteka.models.CategoryOrGenre
import com.ets.onlinebiblioteka.models.Paginated
import com.ets.onlinebiblioteka.models.filters.Kategorija
import com.ets.onlinebiblioteka.models.filters.SelectedFilters
import com.ets.onlinebiblioteka.models.filters.Zanr
import com.ets.onlinebiblioteka.util.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCategoriesOrGenresViewModel : ViewModel() {
    private var data: MutableLiveData<Paginated<CategoryOrGenre>?> = MutableLiveData(null)

    fun getData(): LiveData<Paginated<CategoryOrGenre>?> {
        return data
    }

    fun clearData() {
        data.postValue(null)
    }

    fun loadData(page: Int, categoriesOrGenres: String) {
        if (categoriesOrGenres == "categories") {
            // load all categories
            ApiInterface.create().getKategorije(page).enqueue(object : Callback<Paginated<Kategorija>> {
                override fun onResponse(
                    call: Call<Paginated<Kategorija>>,
                    response: Response<Paginated<Kategorija>>
                ) {
                    response.body()?.let {
                        // transform to category model
                        data.postValue(Paginated(
                            it.currentPage,
                            it.data.map { k ->
                                CategoryOrGenre(k.id, k.name, k.photo, k.description)
                            },
                            it.nextPageUrl,
                            it.prevPageUrl,
                            it.count
                        ))
                    }
                }

                override fun onFailure(call: Call<Paginated<Kategorija>>, t: Throwable) { }
            })
        } else if (categoriesOrGenres == "genres") {
            // load all genres
            ApiInterface.create().getZanrovi(page).enqueue(object : Callback<Paginated<Zanr>> {
                override fun onResponse(
                    call: Call<Paginated<Zanr>>,
                    response: Response<Paginated<Zanr>>
                ) {
                    response.body()?.let {
                        data.postValue(Paginated(
                            // transform to genre model
                            it.currentPage,
                            it.data.map { k ->
                                CategoryOrGenre(k.id, k.name, k.photo, k.description)
                            },
                            it.nextPageUrl,
                            it.prevPageUrl,
                            it.count
                        ))
                    }
                }

                override fun onFailure(call: Call<Paginated<Zanr>>, t: Throwable) { }
            })
        }
    }
}