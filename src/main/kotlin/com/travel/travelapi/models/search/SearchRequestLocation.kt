package com.travel.travelapi.models.search

import com.travel.travelapi.controllers.ExplorePageController
import com.travel.travelapi.models.LatLng

class SearchRequestLocation(
        categoriesSelected: List<Int> = listOf(),
        tagsSelected: List<Int> = listOf(),
        freePlacesOnly: Boolean = false,
        distance: Int? = null,
        val exploreLocation: ExplorePageController.ExploreLocation
): SearchRequest(categoriesSelected, tagsSelected, freePlacesOnly, distance)