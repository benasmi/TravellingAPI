package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Recommendation(
        var name: String? = null,
        var subtitle: String? = null,
        var description: String? = null,
        var type: Int? = null,
        var user: Int? = null,
        var id: Int? = null,
        var objects: Collection<RecommendationObject>? = null)

enum class RecommendationType(val id: Int){
    PLACE(1),
    TOUR(2)
}
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "taipas", defaultImpl = RecommendationPlaces::class)
@JsonSubTypes(
        JsonSubTypes.Type(value = RecommendationPlaces::class, name = "1"),
        JsonSubTypes.Type(value = RecommendationTours::class, name = "2"))
abstract class RecommendationObject(
        var name: String? = null,
        var description: String? = null,
        var id: Int? = null,
        var photos: List<Photo>? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
class RecommendationPlaces(
        name: String? = null,
        description: String? = null,
        id: Int? = null,
        photos: List<Photo>? = null
): RecommendationObject(name, description, id, photos){
    companion object{
        fun createFromPlaceInstance(place: PlaceLocal): RecommendationPlaces{
            return RecommendationPlaces(place.name, place.description, place.placeId, if(place.photos != null && place.photos!!.count() > 0) arrayListOf(place.photos!![0]) else arrayListOf())
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class RecommendationTours(
        name: String? = null,
        description: String? = null,
        id: Int? = null,
        photos: List<Photo>? = null
) : RecommendationObject(name, description, id, photos){
    companion object{
        fun createFromTourInstance(tour: Tour): RecommendationTours{
            return RecommendationTours(tour.name, tour.description, tour.tourId, tour.photos)
        }
    }
}