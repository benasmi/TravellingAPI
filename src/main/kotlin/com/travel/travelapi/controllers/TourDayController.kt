package com.travel.travelapi.controllers

import com.travel.travelapi.models.Tour
import com.travel.travelapi.models.TourApiPlaceInfo
import com.travel.travelapi.models.TourDayDetails
import com.travel.travelapi.models.TourLocalPlaceInfo
import com.travel.travelapi.services.TourDayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class TourDayController(@Autowired private val tourDayService: TourDayService,
                        @Autowired private val transportController: TransportController) {


    /**
     * Get local places for every tour day
     * @param tourId of a tour
     */
    fun getPlacesForTourDay(tourId: Int): ArrayList<TourLocalPlaceInfo>{
        val places = tourDayService.getLocalPlacesByTourId(tourId)
        for(p: TourLocalPlaceInfo in places)
            p.transportFrom = transportController.getLocalPlaceTransportFrom(p.id!!)
        return places
    }

    /**
     * Get api places for every tour day
     * @param tourId of a tour
     */
    fun getApiPlacesForTourDay(tourId: Int): ArrayList<TourApiPlaceInfo>{
        val places = tourDayService.getApiPlacesByTourId(tourId)
        for(p: TourApiPlaceInfo in places)
            p.transportFrom = transportController.getApiPlaceTransportFrom(p.id!!)
        return places
    }

    /**
     * Get tour days info
     * @param tourId of a tour
     */
    fun getTourDaysDetails(tourId: Int): ArrayList<TourDayDetails>{
        return tourDayService.getTourDaysDetails(tourId)
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