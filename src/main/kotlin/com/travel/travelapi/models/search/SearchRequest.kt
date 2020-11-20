package com.travel.travelapi.models.search

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.travel.travelapi.controllers.ExplorePageController
import com.travel.travelapi.models.PlaceApi
import com.travel.travelapi.models.PlaceLocal


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = PlaceLocal::class)
@JsonSubTypes(
        JsonSubTypes.Type(value = SearchRequestCoordinates::class, name = "coordinates"),
        JsonSubTypes.Type(value = SearchRequestBounds::class, name = "bounds"),
        JsonSubTypes.Type(value = SearchRequestLocation::class, name = "location"))
abstract class SearchRequest(
        val categoriesSelected: List<Int> = listOf(),
        val tagsSelected: List<Int> = listOf(),
        val freePlacesOnly: Boolean = false
){
    fun filterEnabled(): Boolean{
        return !(categoriesSelected.isEmpty() && tagsSelected.isEmpty() && !freePlacesOnly)
    }
}
