package com.travel.travelapi.services

import com.travel.travelapi.models.*
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository


@Repository
interface TourDayService {
    fun getLocalPlacesByTourId(@Param("tourDayId") id: Int): ArrayList<TourPlace>
    fun dayTotalDistanceAndTime(@Param("tourDayId") id: Int): TransportFrom
    fun sumPlacesAverageTimeSpent(@Param("tourDayId") id: Int): Int
    fun getTourDaysDetails(@Param("id") id:Int): ArrayList<TourDay>
    fun deleteTourDaysById(@Param("id") id: Int)
    fun insertTourDaysById(@Param("id") id: Int, @Param("tour") tour: Tour)
}