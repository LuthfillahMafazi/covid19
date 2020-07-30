package com.zii.covid19

data class AllCountry(
    val Global: Dunia,
    val Countries: List<Negara>
)

data class Dunia(
    val NewConfirmed:String,
    val TotalConfirmed:String,
    val NewDeaths:String,
    val TotalDeaths:String,
    val NewRecovered:String,
    val TotalRecovered:String

)
data class Negara(
    val Country: String? = null,
    val CountryCode: String? = null,
    val Slug: String? = null,
    val NewConfirmed: String? = null,
    val TotalConfirmed: String? = null,
    val NewDeaths: String? = null,
    val TotalDeaths: String? = null,
    val NewRecovered: String? = null,
    val TotalRecovered: String? = null,
    val Date: String? = null

)

data class InfoCountry(
    val Deaths: String,
    val Confirmed: String,
    val Recovered: String,
    val Active: String,
    val Date: String
)