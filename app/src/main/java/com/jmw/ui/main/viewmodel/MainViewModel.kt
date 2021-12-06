package com.jmw.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import com.jmw.data.model.Country
import com.jmw.data.repository.MainRepository

class MainViewModel(private val mainRepository: MainRepository,file : String) : ViewModel() {

    private var countries: List<Country>? = null


    init {
        fetchCountries(file)
    }

    private fun fetchCountries(file : String) {
        countries = mainRepository.getCountries(file)

    }

    fun getCountries(): List<Country>? {
        return countries
    }

}