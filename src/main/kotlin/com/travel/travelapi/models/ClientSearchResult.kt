package com.travel.travelapi.models

data class ClientSearchResult(
        val places: ObjectCollection? = null,
        val tours: ObjectCollection? = null,
        val locations: List<SearchResultRegion>? = null
)