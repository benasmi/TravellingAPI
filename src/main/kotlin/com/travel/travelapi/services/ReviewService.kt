package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.github.pagehelper.PageInfo
import com.travel.travelapi.models.Review
import com.travel.travelapi.models.ReviewType
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface ReviewService {

    fun insertReview(@Param("review") review: Review)

    fun getUserPlaceReview(@Param("objectId") objectId: Int,
                           @Param("userId") userId: Int,
                           @Param("reviewType") reviewType: Int) : Review?

    fun getAllObjectReviews(@Param("objectId") objectId: Int,
                            @Param("reviewType") reviewType: Int,
                            @Param("userId") userId: Int) : Page<Review>

    fun upVoteReview(@Param("reviewId") reviewId: Int, @Param("userId") userId: Int)
    fun reportReview(@Param("reviewId") reviewId: Int, @Param("userId") userId: Int)

    fun downVoteReview(@Param("reviewId") reviewId: Int, @Param("userId") userId: Int)

    fun alreadyUpVoted(@Param("reviewId") reviewId: Int,
                       @Param("userId") userId: Int): Boolean

    fun alreadyReported(@Param("reviewId") reviewId: Int,
                       @Param("userId") userId: Int): Boolean

    fun getReviewUpVotesCount(@Param("reviewId") reviewId: Int): Int

    fun getObjectTotalRating(@Param("objectId") objectId: Int,
                             @Param("reviewType") reviewType: Int): Float

    fun getObjectTotalReviewsCount(@Param("objectId") objectId: Int,
                             @Param("reviewType") reviewType: Int): Int

    fun getDisplayName(@Param("reviewId") reviewId: Int): String
    fun getDisplayPhoto(@Param("reviewId") reviewId: Int): String
    fun getGender(@Param("reviewId") reviewId: Int): Int

}
