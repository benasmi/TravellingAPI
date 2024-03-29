package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = PlaceLocal::class)
@JsonSubTypes(
        JsonSubTypes.Type(value = PlaceLocal::class, name = "0"),
        JsonSubTypes.Type(value = PlaceApi::class, name = "1"))
abstract class Place (
        var name: String? = null,
        var description: String? = null,
        val hasSchedule: Boolean? = null,
        var latitude: Float? = null,
        var longitude: Float? = null,
        var address: String? = null,
        var country: String? = null,
        var city: String? = null,
        var county: String? = null,
        val municipality: String? = null,
        var phoneNumber: String? = null,
        var website: String? = null,
        var overallStarRating: Double? = null,
        var price: String? = null,
        var userId: Int? = null,
        var categories: List<Category>? = null,
        var grade: Int? = null){


    var parking: List<Parking>? = null
    var schedule: List<WorkingSchedule>? = null
    var photos: List<Photo>? = null
    var tags: List<Tag>? = null
    var sources: List<Source>? = null
}
