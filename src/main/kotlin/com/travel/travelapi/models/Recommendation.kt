package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlin.random.Random

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "collectionType", defaultImpl = Recommendation::class)
@JsonSubTypes(
        JsonSubTypes.Type(value = Recommendation::class, name = "recommendation"),
        JsonSubTypes.Type(value = SuggestionByCategory::class, name = "suggestion_category"),
        JsonSubTypes.Type(value = MiscellaneousCollection::class, name = "other"))
abstract class ObjectCollection(
    var name: String? = null,
    var subtitle: String? = null,
    var objects: Collection<CollectionObject>? = null,
    var id: Int? = null
)

class SuggestionByCategory(
        val category: Category? = null,
        objects: Collection<CollectionObject>? = null
): ObjectCollection(name=category!!.name, subtitle="", objects=objects, id=category.categoryId)

@JsonInclude(JsonInclude.Include.NON_NULL)
class Recommendation(
        name: String? = null,
        subtitle: String? = null,
        objects: Collection<CollectionObject>? = null,
        var description: String? = null,
        var user: Int? = null,
        var type: Int? = null,
        id: Int? = null): ObjectCollection(name, subtitle, objects, id){}

@JsonInclude(JsonInclude.Include.NON_NULL)
class MiscellaneousCollection(
        name: String? = null,
        subtitle: String? = null,
        objects: Collection<CollectionObject>? = null,
        id: Int? = Random.nextInt(1000,1000000)
): ObjectCollection(name, subtitle, objects, id){}

enum class RecommendationType(val id: Int){
    PLACE(1),
    TOUR(2)
}
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(visible = true, use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = CollectionObjectPlace::class)
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
        var averageTimeSpent: Int? = null,
        var longitude: Float? = null,
        var latitude: Float? = null,
        var city: String? = null,
        var country: String? = null,
        var county: String? = null,
        var municipality: String? = null,
        var price: String? = null
): CollectionObject(name, description, id, photos){

    fun setData(place: PlaceLocal){
        name = place.name
        description = place.description
        id = place.placeId
        photos = if(place.photos != null && place.photos!!.count() > 0) arrayListOf(place.photos!![0]) else arrayListOf()
        averageTimeSpent = place.averageTimeSpent
        longitude = place.longitude
        latitude = place.latitude
        city = place.city
        country = place.country
        county = place.county
        municipality = place.municipality
        price = place.price
    }

    companion object{
        fun createFromPlaceInstance(place: PlaceLocal): CollectionObjectPlace{
            val item = CollectionObjectPlace()
            item.setData(place)
            return item
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
class CollectionObjectTour(
        name: String? = null,
        description: String? = null,
        id: Int? = null,
        photos: List<Photo>? = null,
        var numObjects: Int? = null,
        var numDays: Int? = null,
        var distanceTotal: Double? = null
) : CollectionObject(name, description, id, photos){

    fun setData(tour: Tour) {
        name = tour.name
        description = tour.description
        id = tour.tourId
        photos = tour.photos
        numObjects = (tour.days ?: arrayListOf()).map { ( it.data ?: arrayListOf<Any>() ).count() }.sumBy { it }
        numDays = (tour.days ?: arrayListOf<Any>()).count()
        distanceTotal = (tour.days ?: arrayListOf()).map { ( it.data ?: arrayListOf() ).sumBy { place -> place.transport?.distance ?: 0 }}.sum().toDouble()
    }

    companion object{
        fun createFromTourInstance(tour: Tour): CollectionObjectTour{
            val item = CollectionObjectTour()
            item.setData(tour)
            return item
        }
    }
}