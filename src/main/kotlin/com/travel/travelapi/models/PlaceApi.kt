package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class PlaceApi(val apiPlaceId: String? = null,
               val apiTypeId: Int? = null,
               name: String? = null,
               description: String? = null,
               averageTimeSpent: String? = null,
               latitude: Float? = null,
               longitude: Float? = null,
               address: String? = null,
               country: String? = null,
               city: String? = null,
               phoneNumber: String? = null,
               website: String? = null): Place(name, description, averageTimeSpent, latitude, longitude, address ,country, city, phoneNumber, website){

    fun merge(place: PlaceApi){
        description = place.description ?: description
    }
}
