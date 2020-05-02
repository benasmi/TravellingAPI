package com.travel.travelapi.controllers

import com.travel.travelapi.models.Parking
import com.travel.travelapi.services.ParkingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class

ParkingController(@Autowired private val parkingService: ParkingService) {

    /**
     * @param placeId of a place
     * @return parking information related to place
     */
    fun getParkingLocationsById(placeId: Int): List<Parking>{
        return parkingService.selectParkingLocationsById(placeId)
    }
}