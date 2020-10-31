package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.ObjectExistsInTripPlanException
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.*
import com.travel.travelapi.services.TourService
import com.travel.travelapi.services.TripPlanService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tripPlan")
class TripPlanController(
        @Autowired private val tripPlanService: TripPlanService,
        @Autowired private val tourController: TourController,
        @Autowired private val placeController: PlaceController
        ){


    @GetMapping("/all")
    @PreAuthorize("hasAuthority('trip_plan:browse')")
    fun browseTripPlans(
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int
    ): PageInfo<TripPlan> {
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        PageHelper.startPage<TripPlan>(p, s)
        val tripPlans = tripPlanService.selectTripPlans(principal.id!!)

        tripPlans.forEach{
            var photo = tripPlanService.photoForTripPlan(it.id!!)
            if (photo == null)
                photo = Photo(2544, "https://www.traveldirection.ax.lt:8080/api/v1/photo/view?photoreference=d2b281f8-d0ae-46d7-9a96-616286f9f720_14Oct2020070758GMT_1602659278117.jpg")
            it.photo = photo
        }

        return PageInfo(tripPlans)
    }

    @PostMapping("/create")
    fun createTripPlan(@RequestBody name: String): TripPlan{
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        //Creating a trip plan
        val tripPlan = TripPlan(name=name)
        tripPlanService.createTripPlan(tripPlan, principal.id!!)
        return tripPlan
    }

    @GetMapping("/remove")
    fun remove(@RequestParam id: Int){
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        //Removing the trip plan
        tripPlanService.removeTripPlan(id, principal.id!!)
    }

    data class TourTripPlanRequest(val tripPlanId: Int, val tourId: Int)
    data class PlaceTripPlanRequest(val tripPlanId: Int, val placeId: Int)

    /**
     * Checks if user has permission to modify the trip plan with the given ID.
     * Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, tripPlanId: Int){
        //Getting the relevant trip plan from database
        val tripPlanOwner = tripPlanService.getTripPlanOwner(tripPlanId)

        //Checking if user has permission to modify all trip plans unconditionally
        if(user.hasAuthority("trip_plan:modify_unrestricted")){
            return
            //Else we check if user has authority to modify their trip plan and the given trip plan is created by them
        }else if(tripPlanOwner != null
                && tripPlanOwner == user.id
                && user.hasAuthority("trip_plan:modify"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }

    /**
     * Checks if user has permission to view the trip plan with the given ID.
     * Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkViewAccess(user: TravelUserDetails, tripPlanId: Int){
        //Getting the relevant trip plan from database
        val tripPlanOwner = tripPlanService.getTripPlanOwner(tripPlanId)

        //Checking if user has permission to view all trip plans unconditionally
        if(user.hasAuthority("trip_plan:view_unrestricted")){
            return
            //Else we check if user has authority to view their trip plan and the given trip plan is created by them
        }else if(tripPlanOwner != null
                && tripPlanOwner == user.id
                && user.hasAuthority("trip_plan:view"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }

    @RequestMapping("/addTour")
    fun addTour(@RequestBody tourTripPlanRequest: TourTripPlanRequest){
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        checkModifyAccess(principal, tourTripPlanRequest.tripPlanId)

        if(tripPlanService.tourExistsInTripPlan(tourTripPlanRequest.tourId, tourTripPlanRequest.tripPlanId))
            throw ObjectExistsInTripPlanException("Tour with id of ${tourTripPlanRequest.tourId} already exists in this tour plan.")

        tripPlanService.addTour(tourTripPlanRequest.tripPlanId, tourTripPlanRequest.tourId)
    }
    @RequestMapping("/addPlace")
    fun addPlace(@RequestBody placeTripPlanRequest: PlaceTripPlanRequest){
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        checkModifyAccess(principal, placeTripPlanRequest.tripPlanId)

        if(tripPlanService.placeExistsInTripPlan(placeTripPlanRequest.placeId, placeTripPlanRequest.tripPlanId))
            throw ObjectExistsInTripPlanException("Place with id of ${placeTripPlanRequest.placeId} already exists in this tour plan.")

        tripPlanService.addPlace(placeTripPlanRequest.tripPlanId, placeTripPlanRequest.placeId)
    }

    @RequestMapping("/deletePlace")
    fun deletePlace(@RequestBody placeTripPlanRequest: PlaceTripPlanRequest){
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        checkModifyAccess(principal, placeTripPlanRequest.tripPlanId)

        tripPlanService.deletePlace(placeTripPlanRequest.tripPlanId, placeTripPlanRequest.placeId)
    }


    @RequestMapping("/deleteTour")
    fun deleteTour(@RequestBody tourTripPlanRequest: TourTripPlanRequest){
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        checkModifyAccess(principal, tourTripPlanRequest.tripPlanId)

        tripPlanService.deleteTour(tourTripPlanRequest.tripPlanId, tourTripPlanRequest.tourId)
    }

    @GetMapping("/viewTours")
    fun viewTours(
            @RequestParam("tripPlanId") tripPlanId: Int,
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int
    ): ExplorePageController.PageInfoCollectionObject{
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        checkViewAccess(principal, tripPlanId)

        PageHelper.startPage<CollectionObject>(p, s)
        val tours = tripPlanService.toursForTripPlan(tripPlanId);

        tours.forEach{tour ->
            tour.setData(tourController.tourOverviewById(tour.id!!))
        }

        return ExplorePageController.PageInfoCollectionObject(tours)
    }

    @GetMapping("/viewPlaces")
    fun viewPlaces(
            @RequestParam(name="tripPlanId") tripPlanId: Int,
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int
    ): ExplorePageController.PageInfoCollectionObject{
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        checkViewAccess(principal, tripPlanId)

        PageHelper.startPage<CollectionObject>(p, s)
        val places = tripPlanService.placesForTripPlan(tripPlanId);

        places.forEach{place ->
            place.setData(placeController.getPlaceById(false, place.id!!))
        }

        return ExplorePageController.PageInfoCollectionObject(places)
    }


}