package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Parking
import com.travel.travelapi.services.ParkingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RequestMapping("/parking")
@RestController
class ParkingController(@Autowired private val parkingService: ParkingService) {


    /**
     * Insert parking
     */
    @RequestMapping("/insert")
    fun insertParkingForPlace(@RequestBody parking: List<Parking>){
        for(p: Parking in parking){
            parkingService.insertParking(p)
        }
    }

    /**
     * Update parking
     */
    @GetMapping("/all")
    fun insertParking(@RequestBody parking: List<Parking>){
        for(c: Parking in parking)
            parkingService.getAllParkingInfo()
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

}