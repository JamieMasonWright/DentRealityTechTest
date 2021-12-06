package com.jmw.data.api

import android.util.Log
import com.google.gson.Gson
import com.jmw.data.model.Country

class ApiServiceImpl : ApiService {

    override fun getCountries(file: String): List<Country> {

        Log.d("Country list", file)

        val gson = Gson()

        val countries: Array<Country> = gson.fromJson(
            file,
            Array<Country>::class.java
        )

        return countries.toList()
    }

}