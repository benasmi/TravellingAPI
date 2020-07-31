package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SearchResultRegion(
        val country: String? = null,
        val county: String? = null,
        val city: String? = null,
        val municipality: String? = null
)
