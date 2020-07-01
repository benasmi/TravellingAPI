package com.travel.travelapi.models

data class Location(val city: String? = null,
                    val address: String? = null,
                    val country: String? = null,
                    val latitude: Float? = null,
                    val longitude: Float? = null,
                    val county: String? = null,
                    val municipality: String? = null)