package com.jmw.data.api

class ApiHelper(private val apiService: ApiService) {

    fun getCountries(file: String) = apiService.getCountries(file)

}