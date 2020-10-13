package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Tour(val tourId: Int? = null,
                val name: String? = null,
                val description: String? = null,
                val lastModified: String? = null,
                val isPublished: Boolean? = null,
                val isVerified: Boolean? = null,
                var userId: Int? = null,
                val totalObjects: Int? = null,
                val totalDays: Int? = null,
                val totalDistance: Double? = null
                ){
    var days: ArrayList<TourDay>? = null
    var photos: ArrayList<Photo>? = null
    var categories: ArrayList<Category>? = null
}


data class TourDay(val description: String? = null,
                   val tourDayId: Int? = null,
                   val day: Int? = null,
                   var totalDistance: Int? = null,
                   var totalDuration: Int? = null,
                   var travellingDuration: Int? = null,
                   var averageTimeSpentDuration: Int? = null,
                   var totalObjects: Int? = null,
                   var data: ArrayList<TourDayInfo>? = null)

data class TourDayInfo(var place: PlaceLocal? = null,
                       val transport: TransportFrom? = null,
                       var note: String? = "")

data class TransportFrom(var fk_transportId: Int? = null,
                         var distance: Int? = null,
                         var duration: Int? =null)

data class TourPlace (
        var fk_placeId: Int? = null,
        var id: Int? = null,
        var position: Int? = null,
        var day: Int? = null,
        val note: String? = null){

    var transport: TransportFrom? = null
    var place: PlaceLocal? = null
}





