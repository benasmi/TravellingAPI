package com.travel.travelapi.models.search

data class SearchPreview(
        val totalResults: Int? = null,
        val categories: List<CategoryAbstractionInfo>? = null
        )