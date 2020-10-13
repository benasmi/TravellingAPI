package com.travel.travelapi.services

import com.github.pagehelper.Page
import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository

@Repository
interface TripPlanService {

    fun createTripPlan(@Param("name") name: String, @Param("userId") userId: Long)
    fun removeTripPlan(@Param("tripPlanId") tripPlanId: Int, @Param("userId") userId: Long)
    fun addTour(@Param("tripPlanId") tripPlanId: Int, @Param("tourId") tourId: Int)
    fun deleteTour(@Param("tripPlanId") tripPlanId: Int, @Param("tourId") tourId: Int)
    fun addPlace(@Param("tripPlanId") tripPlanId: Int, @Param("placeId") placeId: Int)
    fun deletePlace(@Param("tripPlanId") tripPlanId: Int, @Param("placeId") placeId: Int)
    fun getTripPlanOwner(@Param("tripPlanId") tripPlanId: Int): Long?
    fun tourExistsInTripPlan(@Param("tourId") tourId: Int, @Param("tripPlanId") tripPlanId: Int): Boolean
    fun placeExistsInTripPlan(@Param("placeId") placeId: Int, @Param("tripPlanId") tripPlanId: Int): Boolean
    fun toursForTripPlan(@Param("tripPlanId") tripPlanId: Int): Page<CollectionObjectTour>
    fun placesForTripPlan(@Param("tripPlanId") tripPlanId: Int): Page<CollectionObjectPlace>
    fun selectTripPlans(@Param("userId") userId: Long): Page<TripPlan>
    fun selectPhotosForTripPlan(@Param("tripPlanId") tripPlanId: Int): List<Photo>
}