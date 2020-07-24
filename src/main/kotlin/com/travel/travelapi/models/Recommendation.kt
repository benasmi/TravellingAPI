package com.travel.travelapi.models

data class Recommendation(
        var name: String? = null,
        var subtitle: String? = null,
        var description: String? = null,
        var type: Int? = null,
        var user: Int? = null,
        var id: Int? = null,
        var areas: ArrayList<Location>? = null,
        var places: ArrayList<PlaceLocal>? = null,
        var tours: ArrayList<Tour>? = null)

enum class RecommendationType(val id: Int){
    PLACE(1),
    TOUR(2)
}