package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.PlaceReview
import com.travel.travelapi.models.PlaceReviewInfo
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository


@Repository
interface PlaceReviewService {
    fun selectReviewsByPlace(@Param("id") id: Int): List<PlaceReview>
    fun getReviewsCountsByPlace(@Param("id") id: Int): Int
    fun getReviewsAverageRating(@Param("id") id: Int): Float

    fun getReviewsById(@Param("id") id: Int, @Param("orderOption") orderOption: String = "newest"): Page<PlaceReviewInfo>
}