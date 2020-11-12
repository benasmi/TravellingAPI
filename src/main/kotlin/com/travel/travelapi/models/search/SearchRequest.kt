package com.travel.travelapi.models.search

import com.travel.travelapi.controllers.ExplorePageController

data class SearchRequest(
        val categoriesSelected: List<Int>? = null,
        val tagsSelected: List<Int>? = null,
        var distance: Int? = null,
        val freePlacesOnly: Boolean? = null,
        val exploreLocation: ExplorePageController.ExploreLocation
)