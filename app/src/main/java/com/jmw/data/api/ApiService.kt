package com.jmw.data.api

import com.jmw.data.model.Country

interface ApiService {

    fun getCountries(file: String): List<Country>

}