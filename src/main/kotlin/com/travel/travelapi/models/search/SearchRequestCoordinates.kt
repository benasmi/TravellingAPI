package com.travel.travelapi.models.search

import com.travel.travelapi.controllers.ExplorePageController
import com.travel.travelapi.models.LatLng

class SearchRequestCoordinates(
        categoriesSelected: List<Int> = listOf(),
        tagsSelected: List<Int> = listOf(),
        freePlacesOnly: Boolean = false,
        var distance: Int? = null,
        val coordinates: LatLng
): SearchRequest(categoriesSelected, tagsSelected, freePlacesOnly)