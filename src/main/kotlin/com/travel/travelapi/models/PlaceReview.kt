package com.travel.travelapi.models

import java.sql.Date

data class PlaceReview(
        val fk_placeId: Int? = null,
        val fk_reviewId: Int? = null,
        val review: String? = null,
        val rating: Int? = null,
        val upvotes: Int? = null,
        val name: String? = null,
        val surname: String? = null,
        val photoUrl: String? = null,
        val title: String? = null,
        val timeAdded: String? = null
)
