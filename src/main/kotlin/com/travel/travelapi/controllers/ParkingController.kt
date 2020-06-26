package com.travel.travelapi.controllers

import com.travel.travelapi.models.Parking
import com.travel.travelapi.services.ParkingService
import com.travel.travelapi.sphinx.SphinxService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RequestMapping("/parking")
@RestController
class ParkingController(@Autowired private val parkingService: ParkingService,
                  @Autowired private val sphinxService: SphinxService) {

    /**
     * Update parking
     */
    @PostMapping("/all")
    fun getParking(@RequestBody parking: Parking): List<Parking>{
        return parkingService.getAllParkingInfo(parking)
    }

    /**
     * Insert parking
     */
    @PostMapping("/insert")
    fun insertParkingForPlace(@RequestBody parking: List<Parking>): List<Parking>{
        for(p: Parking in parking){
            parkingService.insertParking(p)
        }
        return parking
    }

    /**
     * Delete parking
     */
    @PostMapping("/delete")
    fun deleteCategory(@RequestBody parking: List<Parking>){
        for(c: Parking in parking)
            parkingService.deleteParking(c)
    }

    /**
     * Update parking
     */
    @PostMapping("/update")
    fun updateCategory(@RequestBody parking: List<Parking>){
        for(c: Parking in parking)
            parkingService.updateParking(c)
    }

    //Searches with sphinx
    @GetMapping("/search")
    fun getNearestParkingByLatLng(@RequestParam lat: Double, @RequestParam lng: Double):List<Parking>{
        val ids: List<String> = sphinxService.searchParkingSphinx(lat,lng)

        return if(ids.isEmpty()) ArrayList() else parkingService.search(ids)
    }

    @GetMapping("/searchAdmin")
    fun searchAdmin(@RequestParam lat: Double, @RequestParam lng: Double):List<Parking> {
        return parkingService.searchParking(lat, lng)
    }
}