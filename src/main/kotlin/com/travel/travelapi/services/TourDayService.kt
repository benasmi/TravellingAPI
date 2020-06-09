package com.travel.travelapi.services

import com.travel.travelapi.models.Tour
import com.travel.travelapi.models.TourApiPlaceInfo
import com.travel.travelapi.models.TourDayDetails
import com.travel.travelapi.models.TourLocalPlaceInfo
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository


@Repository
interface TourDayService {
    fun getLocalPlacesByTourId(@Param("id") id: Int): ArrayList<TourLocalPlaceInfo>
    fun getApiPlacesByTourId(@Param("id") id: Int): ArrayList<TourApiPlaceInfo>
    fun getTourDaysDetails(@Param("id") id:Int): ArrayList<TourDayDetails>
    fun deleteTourDaysById(@Param("id") id: Int)
    fun insertTourDaysById(@Param("id") id: Int, @Param("tour") tour: Tour)
}