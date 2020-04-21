package com.travel.travelapi.pojos

data class Parking(val latitude: Float,
                   val longitude: Float,
                   val address: String,
                   val priority: Int,
                   val parkingId: Int)