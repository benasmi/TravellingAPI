package com.travel.travelapi.controllers

import com.travel.travelapi.models.*
import com.travel.travelapi.services.ParkingPlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
     * Delete parking from place
     */
    @RequestMapping("/delete")
    fun deleteParking(@RequestBody parkingInfo: List<ParkingPlace>){
        for(p: ParkingPlace in parkingInfo)
            parkingPlaceService.deleteParkingForPlace(p)

    }

    /**
     * Update place parking
     */
    @RequestMapping("/update")
    fun updateParkingForPlace(@RequestBody parkingPlaces: List<Parking>, @RequestParam(name="p") id: Int){
        parkingPlaceService.deleteParkingForPlaceById(id)

        val parking = ArrayList<ParkingPlace>()
        parkingPlaces.forEachIndexed { index, p ->
            parking.add(ParkingPlace(id, p.parkingId, index))
        }
        insertParking(parking)
    }

}