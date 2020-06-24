package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Tour(val tourId: Int? = null,
                val name: String? = null,
                val description: String? = null,
                val lastModified: String? = null,
                val isPublished: Int? = null,
                val isVerified: Int? = null
                ){
    var days: ArrayList<TourDay>? = null
}

data class TourDayDetails(val description: String? = null)
data class TransportFrom(val fk_transportId: Int? = null)
data class TourDay(val description: String? = null, var data: ArrayList<TourDayInfo>? = null)

data class TourDayInfo(val place: Place? = null,
                       val transport: ArrayList<TransportFrom>? = null)


abstract class TourPlace (
        var id: Int? = null,
        var position: Int? = null,
        var day: Int? = null
        ){
    var transportFrom: ArrayList<TransportFrom>? = null
    var place: Place? = null
}

class TourLocalPlaceInfo(val fk_placeId: Int? = null,
                              id: Int? = null,
                              position: Int? = null,
                              day: Int? = null,
                              val placeType: Int? = 0): TourPlace(id,position,day){
}

class TourApiPlaceInfo(val fk_apiPlaceId: String? = null,
                         id: Int? = null,
                         position: Int? = null,
                         day: Int? = null,
                         val placeType: Int? = 1): TourPlace(id,position,day)



