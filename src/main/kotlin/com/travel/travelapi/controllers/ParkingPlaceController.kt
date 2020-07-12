package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.*
import com.travel.travelapi.services.ParkingPlaceService
import com.travel.travelapi.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/parkingplace")
@RestController
class ParkingPlaceController(@Autowired private val parkingPlaceService: ParkingPlaceService,
@Autowired private val placeService: PlaceService) {

    /**
     * @param placeId of a place
     * @return parking information related to place
     */
    @PreAuthorize("hasAuthority('parkingplace:read')")
    fun getParkingLocationsById(placeId: Int): List<Parking>{
        return parkingPlaceService.selectByPlaceId(placeId)
    }

    /**
     * Map category to a place
     */
    @RequestMapping("/insert")
    fun insertParking(@RequestBody parkingInfo: List<ParkingPlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(p: ParkingPlace in parkingInfo) {

            //Checking if user has access to modify this place's parkings. If not, this line will throw an exception
            checkModifyAccess(principal, p.fk_placeId!!)

            //Inserting parking
            parkingPlaceService.insertParkingForPlace(p)
        }
    }

    /**
     * Checks if user has permission to modify the parking for a given place with the given place ID.
     * Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, placeId: Int){
        //Getting the relevant place from database
        val place = placeService.selectById(placeId)

        //Checking if user has permission to modify all place parkings unconditionally
        if(user.hasAuthority("parkingplace:modify_unrestricted")){
            return
            //Else we check if user has authority to modify their place's parking(s) and the given place is created by them
        }else if(place.userId != null
                && place.userId!!.toLong() == user.id
                && !place.isVerified!!
                && !place.isPublic!!
                && user.hasAuthority("parkingplace:modify"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }

    /**
     * Delete parking from place
     */
    @RequestMapping("/delete")
    fun deleteParking(@RequestBody parkingInfo: List<ParkingPlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(p: ParkingPlace in parkingInfo) {

            //Checking if user has access to modify this place's parkings. If not, this line will throw an exception
            checkModifyAccess(principal, p.fk_placeId!!)

            //Deleting parking for the given place
            parkingPlaceService.deleteParkingForPlace(p)
        }

    }

    /**
     * Update place parking
     */
    @RequestMapping("/update")
    fun updateParkingForPlace(@RequestBody parkingPlaces: List<Parking>, @RequestParam(name="p") id: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to modify this place's parkings. If not, this line will throw an exception
        checkModifyAccess(principal, id)

        parkingPlaceService.deleteParkingForPlaceById(id)

        val parking = ArrayList<ParkingPlace>()
        parkingPlaces.forEachIndexed { index, p ->
            parking.add(ParkingPlace(id, p.parkingId, index))
        }
        insertParking(parking)
    }

}