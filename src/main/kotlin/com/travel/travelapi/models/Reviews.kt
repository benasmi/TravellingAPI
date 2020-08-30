package com.travel.travelapi.models

import java.sql.Date


enum class ReviewType(val reviewType: Int) {
    PLACE(1),
    TOUR(2),
}

data class Review(
        val id: Int? = null,
        val review: String? = null,
        val rating: Float? = null,
        var userId: Int? = null,
        val reviewDate: String? = null,
        val objectId: Int? = null,
        val reviewType: Int? = ReviewType.PLACE.reviewType,
        var upVotes: Int = 0,
        var userUpVoted: Boolean = false,
        var displayName: String? = "",
        var userPhoto: String? = "",
        var gender: Int? = 1,
        var userReported: Boolean? = false
)