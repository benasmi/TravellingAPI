package com.travel.travelapi.controllers

import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.models.Parking
import com.travel.travelapi.models.ParkingPlace
import com.travel.travelapi.models.PhotoPlace
import com.travel.travelapi.services.ParkingPlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/parkingplace")
@RestController
class ParkingPlaceController(@Autowired private val parkingPlaceService: ParkingPlaceService) {

    /**
     * @param placeId of a place
     * @return parking information related to place
     */
    fun getParkingLocationsById(placeId: Int): List<Parking>{
        return parkingPlaceService.selectByPlaceId(placeId)
    }

    /**
     * Map category to a place
     */
    @RequestMapping("/insert")
    fun insertParking(@RequestBody parkingInfo: List<ParkingPlace>){
        for(p: ParkingPlace in parkingInfo)
            parkingPlaceService.insertParkingForPlace(p)
    }

    /**
     * Update parking priority
     */
    @RequestMapping("/update")
    fun updatePhotoIndexesForPlace(@RequestBody parkingInfo: List<ParkingPlace>){
        for(p: ParkingPlace in parkingInfo)
            parkingPlaceService.updateParkingIndexing(p)
    }

    /**
     * Delete parking from place
     */
    @RequestMapping("/delete")
    fun deleteParking(@RequestBody parkingInfo: List<ParkingPlace>){
        for(p: ParkingPlace in parkingInfo)
            parkingPlaceService.deleteCategoryForPlace(p)

    }

}