package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.controllers.RecommendationController
import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface RecommendationService {

    /**
     * This will create a recommendation inside our database.
     */
    fun createRecommendation(@Param("recommendation") recommendation: Recommendation): Int

    fun selectRecommendationById(@Param("id") id: Int): Recommendation

    fun removeRecommendation(@Param("id") id: Int)

    fun searchRecommendations(@Param("keyword") keyword: String?, @Param("type") type: Int?): Page<Recommendation>

    fun addPlace(@Param("data") placeRecommendation: RecommendationController.ObjectRecommendation)

    fun addTour(@Param("data") tourRecommendation: RecommendationController.ObjectRecommendation)

    fun removePlace(@Param("data") placeRecommendation: RecommendationController.ObjectRecommendation)

    fun removeTour(@Param("data") tourRecommendation: RecommendationController.ObjectRecommendation)

    fun tourExists(@Param("recommendationId") recommendationId: Int, @Param("tourId") tourId: Int): Boolean

    fun placeExists(@Param("recommendationId") recommendationId: Int, @Param("placeId") placeId: Int): Boolean

    fun matchRecommendationsByLocation(@Param("locationType") locationType: String,
                                       @Param("location") location: String,
                                       @Param("latLng") latLng: List<String>,
                                       @Param("range") range: Double): List<Recommendation>

    fun findRecsRelatedWithPlaceIds(@Param("ids") ids: List<Int>): List<Int>
    /**
     * Returns a list of places, that have id's for a recommendation with a given ID
     */
    fun selectPlacesForRecommendation(@Param("recommendationId") recommendationId: Int):ArrayList<Int>

    fun selectToursForRecommendation(@Param("recommendationId") recommendationId: Int):ArrayList<Tour>

    fun updateRecommendation(@Param("recommendation") recommendation: Recommendation)

}