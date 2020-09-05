package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.Parking
import com.travel.travelapi.services.ParkingService
import com.travel.travelapi.sphinx.SphinxService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RequestMapping("/parking")
@RestController
class ParkingController(@Autowired private val parkingService: ParkingService,
                  @Autowired private val sphinxService: SphinxService) {

    /**
     * Searches for parking by address using levenshtein distance
     */
    @PostMapping("/all")
    @PreAuthorize("hasAuthority('parking:read_unrestricted')")
    fun getParking(@RequestBody parking: Parking): List<Parking>{
        return parkingService.getAllParkingInfo(parking)
    }

    /**
     * Insert parking
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('parking:add')")
    fun insertParkingForPlace(@RequestBody parking: List<Parking>): List<Parking>{
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(p: Parking in parking){
            p.userId = principal.id?.toInt()
            parkingService.insertParking(p)
        }
        return parking
    }

    /**
     * Checks if user has permission to modify the parking with the given ID. Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, parkingId: Int){

        //Getting the relevant parking from database
        val parking = parkingService.getParkingById(parkingId)

        //Checking if user has permission to modify all parkings unconditionally
        if(user.hasAuthority("parking:modify_unrestricted")){
            return
        //Else we check if user has authority to modify their parking(s) and the given parking is created by them
        }else if(parking.userId != null
                && parking.userId?.toLong() == user.id
                && user.hasAuthority("parking:modify"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }

    /**
     * Delete parking
     */
    @PostMapping("/delete")
    fun deleteParking(@RequestBody parking: List<Parking>){
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(c: Parking in parking) {
            //Checking if user has permission to modify this parking. This line throws an exception if user is not permitted.
            checkModifyAccess(principal, c.parkingId!!)

            //Deleting the parking
            parkingService.deleteParking(c)
        }
    }

    /**
     * Update parking
     */
    @PostMapping("/update")
    fun updateParking(@RequestBody parking: List<Parking>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(c: Parking in parking) {
            //Checking if user has permission to modify this parking. This line throws an exception if user is not permitted.
            checkModifyAccess(principal, c.parkingId!!)

            //Updating the parking
            parkingService.updateParking(c)
        }
    }

    //Searches with sphinx
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('parking:read_unrestricted')")
    fun getNearestParkingByLatLng(@RequestParam lat: Double, @RequestParam lng: Double):List<Parking>{
        val ids: List<String> = sphinxService.searchParkingSphinx(lat,lng)

        return if(ids.isEmpty()) ArrayList() else parkingService.search(ids)
    }

    @GetMapping("/searchAdmin")
    @PreAuthorize("hasAuthority('parking:read_unrestricted')")
    fun searchAdmin(@RequestParam lat: Double, @RequestParam lng: Double):List<Parking> {
        return parkingService.searchParking(lat, lng)
    }
}