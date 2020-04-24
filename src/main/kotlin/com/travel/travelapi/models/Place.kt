package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.WritableTypeId
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Place (val name: String? = null,
                  val description: String? = null,
                  val placeId: Int? = null,
                  val averageTimeSpent: String? =  null,
                  val latitude: Float? = null,
                  val longitude: Float? = null,
                  val address: String? = null,
                  val country: String? = null,
                  val city: String? = null,
                  val phoneNumber: String? = null,
                  val website: String? = null){

    var categories: List<Category>? = null
    var parking: List<Parking>? = null
    var reviews: List<Review>? = null
    var schedule: List<WorkingSchedule>? = null
    var photos: List<Photo>? = null
    var tags: List<Tag>? = null

}
