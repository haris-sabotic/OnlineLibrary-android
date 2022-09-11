package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.models.filters.SelectedFilters

class KnjigeViewModel : ViewModel() {
    private var selectedFilters: MutableLiveData<SelectedFilters?> = MutableLiveData(null)

    fun getSelectedFilters(): LiveData<SelectedFilters?> {
        return selectedFilters
    }
    fun setSelectedFilters(_selectedFilters: SelectedFilters?) {
        selectedFilters.postValue(_selectedFilters)
    }
}