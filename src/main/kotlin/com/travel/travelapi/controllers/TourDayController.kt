package com.travel.travelapi.controllers

import com.travel.travelapi.models.*
import com.travel.travelapi.services.TourDayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class TourDayController(@Autowired private val tourDayService: TourDayService,
                        @Autowired private val transportController: TransportController) {


    /**
     * Get local places for tour day
     * @param tourId of a tour
     */
    fun getPlacesForTourDay(tourDayId: Int): ArrayList<TourPlace>{
        val places = tourDayService.getLocalPlacesByTourId(tourDayId)
        for(p: TourPlace in places)
            p.transport = transportController.getLocalPlaceTransportFrom(p.id!!)
        return places
    }


    /**
     * Get tour days info
     * @param tourId of a tour
     */
    fun getTourDaysDetails(tourId: Int): ArrayList<TourDay>{
        val days = tourDayService.getTourDaysDetails(tourId)
        days.forEach {
            val transportInfo = tourDayService.dayTotalDistanceAndTime(it.tourDayId!!)
            it.totalDistance = transportInfo.distance
            it.travellingDuration = transportInfo.duration

            val arr = ArrayList<Int>()
            it.data?.forEach {dayObj ->
                arr.add(dayObj.place?.placeId!!)
            }
            val avgTimeSpent = tourDayService.sumPlacesAverageTimeSpent(it.tourDayId)
            it.averageTimeSpentDuration = avgTimeSpent
            it.totalDuration = avgTimeSpent + transportInfo.duration!!
            it.totalObjects = tourDayService.getDayObjectsCount(it.tourDayId)
        }
        return days
    }

    /**
     * Delete days from tour
     * @param tourId of a tour
     */
    fun deleteTourDaysById(tourId: Int){
        tourDayService.deleteTourDaysById(tourId)
    }

    /**
     * Insert tour days with places
     * @param tourId of a tour
     * @param tour
     */
    fun insertTourDaysById(tourId: Int, tour: Tour){
        tourDayService.insertTourDaysById(tourId, tour)
    }

}