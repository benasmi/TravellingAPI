package com.travel.travelapi.services

import com.travel.travelapi.models.Review
import com.travel.travelapi.models.WorkingSchedule
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface ReviewService {

    @Select("SELECT * FROM REVIEW WHERE fk_placeId=#{id} limit #{lim}")
    fun selectReviewsById(@Param("id") id: Int, @Param("lim") lim: Int=Int.MAX_VALUE): List<Review>

}