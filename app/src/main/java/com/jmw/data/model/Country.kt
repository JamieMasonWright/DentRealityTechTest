package com.jmw.data.model

import com.google.gson.annotations.SerializedName

data class Country(

    @SerializedName("timezones")
    val timezones: List<String>? = null,
    @SerializedName("latlng")
    val latlng: List<String>? = null,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("country_code")
    val country_code: String = "",
    @SerializedName("capital")
    val capital: String = ""

)




