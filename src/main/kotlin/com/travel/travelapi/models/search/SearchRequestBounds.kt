package com.travel.travelapi.models.search

import com.travel.travelapi.models.LatLng

class SearchRequestBounds(
        categoriesSelected: List<Int> = listOf(),
        tagsSelected: List<Int> = listOf(),
        freePlacesOnly: Boolean = false,
        var leftEdge: LatLng,
        var rightEdge: LatLng
): SearchRequest(categoriesSelected, tagsSelected, freePlacesOnly)