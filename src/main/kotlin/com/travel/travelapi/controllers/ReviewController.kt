package com.travel.travelapi.controllers

import com.travel.travelapi.models.Review
import com.travel.travelapi.services.ReviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class ReviewController(@Autowired private val reviewPlaceService: ReviewService) {

    /**
     * @param placeId of a place
     * @return review information related to place
     */
    fun getReviewsById(objectId: Int):List<Review>{
        return reviewPlaceService.selectReviewsById(objectId)
    }
}