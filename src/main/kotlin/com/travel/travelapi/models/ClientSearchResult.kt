package com.travel.travelapi.models

data class ClientSearchResult(
        val places: List<PlaceLocal>? = null,
        val tours: List<Tour>? = null,
        val locations: List<SearchResultRegion>? = null
)