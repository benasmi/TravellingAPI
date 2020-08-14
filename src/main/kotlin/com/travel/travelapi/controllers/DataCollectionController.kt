package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.models.SearchedLocation
import com.travel.travelapi.models.SearchedPlace
import com.travel.travelapi.models.SearchedTour
import com.travel.travelapi.services.DataCollectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DataCollectionController(@Autowired private val dataCollectionService: DataCollectionService) {


    /**
     * Inserts search of a place as a row in mysql(Date,Device,User,Place)
     */
    fun searchedPlace(placeId: Int){
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        dataCollectionService.insertSearchedPlace(
                SearchedPlace(
                        fk_placeId = placeId,
                        fk_userId = principal.id?.toInt(),
                        device = principal.device
                ))
    }

    /**
     * Inserts search of a tour as a row in mysql(Date,Device,User,Tour)
     */
    fun searchedTour(tourId: Int){
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        dataCollectionService.insertSearchedTour(
                SearchedTour(
                        fk_tourId = tourId,
                        fk_userId = principal.id?.toInt(),
                        device = principal.device
                ))
    }

    /**
     * Inserts search of a location as row in mysql(Date,Device,User,Location, Type)
     */
    fun searchedLocation(exploreLocation: ExplorePageController.ExploreLocation){
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        dataCollectionService.insertSearchedLocation(
                SearchedLocation(
                        exploreLocation = exploreLocation,
                        fk_userId = principal.id?.toInt(),
                        device = principal.device
                ))
    }

}
