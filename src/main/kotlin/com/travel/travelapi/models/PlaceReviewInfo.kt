package com.travel.travelapi.models

data class PlaceReviewInfo(
        val reviews: List<PlaceReview>? = null,
        val total: Int? = 0,
        val rating: Float? = 0f)