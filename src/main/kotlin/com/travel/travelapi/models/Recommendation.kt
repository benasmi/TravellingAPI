package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "collectionType", defaultImpl = Recommendation::class)
@JsonSubTypes(
        JsonSubTypes.Type(value = Recommendation::class, name = "recommendation"),
        JsonSubTypes.Type(value = SuggestionByCategory::class, name = "suggestion_category"),
        JsonSubTypes.Type(value = MiscellaneousCollection::class, name = "other"))
abstract class ObjectCollection(
    var name: String? = null,
    var subtitle: String? = null,
    var objects: Collection<CollectionObject>? = null
)

class SuggestionByCategory(
        val category: Category? = null,
        objects: Collection<CollectionObject>? = null
): ObjectCollection(name=category!!.name, subtitle="", objects=objects)

@JsonInclude(JsonInclude.Include.NON_NULL)
class Recommendation(
        name: String? = null,
        subtitle: String? = null,
        objects: Collection<CollectionObject>? = null,
        var description: String? = null,
        var user: Int? = null,
        var id: Int? = null,
        var type: Int? = null
        ): ObjectCollection(name, subtitle, objects){}

@JsonInclude(JsonInclude.Include.NON_NULL)
class MiscellaneousCollection(
        name: String? = null,
        subtitle: String? = null,
        objects: Collection<CollectionObject>? = null
): ObjectCollection(name, subtitle, objects){}

enum class RecommendationType(val id: Int){
    PLACE(1),
    TOUR(2)
}
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = CollectionObjectPlace::class)
@JsonSubTypes(
        JsonSubTypes.Type(value = CollectionObjectPlace::class, name = "place"),
        JsonSubTypes.Type(value = CollectionObjectTour::class, name = "tour"))
abstract class CollectionObject(
        var name: String? = null,
        var description: String? = null,
        var id: Int? = null,
        var photos: List<Photo>? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
class CollectionObjectPlace(
        name: String? = null,
        description: String? = null,
        id: Int? = null,
        photos: List<Photo>? = null,
        val averageTimeSpent: Int? = null,
        val city: String? = null,
        val country: String? = null,
        val county: String? = null,
        val municipality: String? = null,
        val price: String? = null
): CollectionObject(name, description, id, photos){
    companion object{
        fun createFromPlaceInstance(place: PlaceLocal): CollectionObjectPlace{
            return CollectionObjectPlace(place.name, place.description, place.placeId, if(place.photos != null && place.photos!!.count() > 0) arrayListOf(place.photos!![0]) else arrayListOf(), place.averageTimeSpent,place.city,place.country,place.county,place.municipality,place.price)
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class CollectionObjectTour(
        name: String? = null,
        description: String? = null,
        id: Int? = null,
        photos: List<Photo>? = null
) : CollectionObject(name, description, id, photos){
    companion object{
        fun createFromTourInstance(tour: Tour): CollectionObjectTour{
            return CollectionObjectTour(tour.name, tour.description, tour.tourId, tour.photos)
        }
    }
}