package com.jmw.data.repository

import com.jmw.data.api.ApiHelper
import com.jmw.data.model.Country

class MainRepository(private val apiHelper: ApiHelper) {

    fun getCountries(file : String): List<Country> {
        return apiHelper.getCountries(file)
    }

}