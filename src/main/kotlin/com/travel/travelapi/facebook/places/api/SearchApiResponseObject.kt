package com.travel.travelapi.facebook.places.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.travel.travelapi.models.PlaceApi

@JsonDeserialize(using = FacebookPlaceDeserializer::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class SearchApiResponseObject(
        val data: List<PlaceApi>? = null,
        val before: String? = null,
        val after: String? = null
){

}