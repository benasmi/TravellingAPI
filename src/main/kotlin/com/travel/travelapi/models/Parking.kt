package com.travel.travelapi.models


data class Parking(val fk_apiPlaceId: Int? = null,
                   val latitude: Float? = null,
                   val longitude: Float? = null,
                   val address: String? = null,
                   val priority: Int? = null,
                   val parkingId: Int? = null)

