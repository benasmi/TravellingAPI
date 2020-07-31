package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.facebook.places.api.ApiPlaceController
import com.travel.travelapi.models.*
import com.travel.travelapi.services.TourService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/tour")
class TourController(@Autowired private val tourService: TourService,
                     @Autowired private val tourDayController: TourDayController,
                     @Autowired private val placeController: PlaceController,
                     @Autowired private val apiPlaceController: ApiPlaceController) {

    /**
     * Checks if user has permission to modify the tour with a given ID.
     * Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, tourId: Int){
        //Getting the relevant tour from database
        val tour = tourService.getTourById(tourId)

        //Checking if user has permission to modify tour info unconditionally
        if(user.hasAuthority("tour:modify_unrestricted")){
            return
            //Else we check if user has authority to modify their tour and the given tour is created by them
        }else if(tour.userId != null
                && tour.userId!!.toLong() == user.id
                && !tour.isVerified!!
                && !tour.isPublished!!
                && user.hasAuthority("tour:modify"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }


    /**
     * @param keyword of a place
     * @param p is page number
     * @param s is page size
     * @return paged tour data for admin
     */
    @GetMapping("/searchadmin")
    @PreAuthorize("hasAuthority('tour:read_unrestricted')")
    fun getPlacesAdmin(@RequestParam(required = true, defaultValue = "", name = "keyword") keyword: String,
                       @RequestParam(required = false, defaultValue = "1", name = "p") p: Int,
                       @RequestParam(required = false, defaultValue = "10", name = "s") s: Int,
                       @RequestParam("o", defaultValue = "", name = "o") o: String ): PageInfo<Tour> {

        PageHelper.startPage<Tour>(p, s)
        val filterOptions = o.split(",")
        val tours = tourService.selectAllAdmin(keyword,filterOptions)
        return PageInfo(tours)
    }

    fun tourOverviewById(tourId: Int): Tour{
        //Getting tour by ID
        val tour: Tour = tourService.getTourById(tourId)
        //Creating a new tour object, with limited amount of information
        val tourTrimmed = Tour(tourId = tour.tourId, name=tour.name, description=tour.description,userId=tour.userId)
        //Selecting all photos of all tour places
        val photos = tourService.photosForTour(tourId)
        //Selecting first photo to be the featured photo for this tour. This should be changed to something
        //else in the future
        tourTrimmed.photos = if (photos.count() > 0) arrayListOf(photos[0]) else null
        return tourTrimmed
    }

    /**
     * Get tour by id
     * @param id of a tour
     * @return full tour information with days and places
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('tour:read')")
    fun getTourById(@RequestParam(name = "id", required = true) id: Int): Tour {
        val tour: Tour = tourService.getTourById(id)

        val localPlaces = tourDayController.getPlacesForTourDay(tour.tourId!!)
        val apiPlaces = tourDayController.getApiPlacesForTourDay(tour.tourId)
        val tourDaysDetails = tourDayController.getTourDaysDetails(tour.tourId)

        val places: ArrayList<TourPlace> = ArrayList()
        places.addAll(localPlaces)
        places.addAll(apiPlaces)

        for(p: TourPlace in places){
            when(p){
                is TourLocalPlaceInfo ->{
                    p.place = placeController.getPlaceById(full = false, id = p.fk_placeId!!)
                }
                is TourApiPlaceInfo ->{
                    p.place = apiPlaceController.search(p.fk_apiPlaceId!!)
                }
            }
        }
        val days: ArrayList<TourDay> = ArrayList()
        val grouped = places.groupBy { it.day }.values

        tourDaysDetails.forEach{
            days.add(TourDay(it.description,ArrayList()))
        }

        grouped.forEachIndexed { index, list ->
            val tourDay = ArrayList<TourDayInfo>()
            list.sortedBy {(it.position) }.forEach {
                tourDay.add(TourDayInfo(it.place!!, it.transportFrom!!))
            }
            days[index].data = tourDay
        }
        tour.days = days
        return tour
    }

    /**
     * Update tour info
     * @param tour
     * @param id of a tour
     */
    @PostMapping("/update")
    fun updateTourById(@RequestBody tour: Tour, @RequestParam("id") id: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to modify this tour. If not, this line will throw an exception
        checkModifyAccess(principal, id)

        tourService.updateTour(tour, id)
        tourDayController.deleteTourDaysById(id)
        tourDayController.insertTourDaysById(id, tour)
    }

    /**
     * Delete tour by id
     * @param id of a tour
     */
    @GetMapping("/delete")
    fun deleteTourById(@RequestParam(name = "id") id: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to modify this tour. If not, this line will throw an exception
        checkModifyAccess(principal, id)

        tourService.deleteTourById(id)
    }


    /**
     * Insert new tour
     * @param tour
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('tour:modify')")
    fun insertTour(@RequestBody tour: Tour): Int{
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        tour.userId = principal.id!!.toInt()
        tourService.insertTour(tour)
        return tour.tourId!!
    }

    @GetMapping("/changeVerificationStatus")
    @PreAuthorize("hasAuthority('tour:verify')")
    fun changeVerificationStatus(@RequestParam("id") tourId: Int, @RequestParam("verified") isVerified: Boolean){
        tourService.changeVerificationStatus(tourId, isVerified)
    }

    @GetMapping("/changePublicityStatus")
    fun changePublicityStatus(@RequestParam("id") tourId: Int, @RequestParam("public") isPublic: Boolean){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to modify this tour. If not, this line will throw an exception
        checkModifyAccess(principal, tourId)

        tourService.changePublicityStatus(tourId, isPublic)
    }

    @RestController
    @RequestMapping("/tour/categories")
    class Tags(@Autowired private val tourService: TourService){

        /**
         * Checks if user has permission to modify the tour's categories with a given ID.
         * Throws an exception if user is not permitted
         */
        @Throws(UnauthorizedException::class)
        fun checkModifyAccess(user: TravelUserDetails, tourId: Int){
            //Getting the relevant tour from database
            val tour = tourService.getTourById(tourId)

            //Checking if user has permission to modify tour info unconditionally
            if(user.hasAuthority("tourcategory:modify_unrestricted")){
                return
                //Else we check if user has authority to modify their tour and the given tour is created by them
            }else if(tour.userId != null
                    && tour.userId!!.toLong() == user.id
                    && !tour.isVerified!!
                    && !tour.isPublished!!
                    && user.hasAuthority("tourcategory:modify"))   {
                return
            }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
        }

        /**
         * Get categories for tour
         * @param id of a tour
         */
        @GetMapping("")
        @PreAuthorize("hasAuthority('tour:read')")
        fun categories(@RequestParam(name = "id") id: Int): List<Category>{
            return tourService.getCategoriesForTour(id)
        }

        /**
         * Set categories for a tour
         */
        @RequestMapping("/update")
        fun updateTourCategories(@RequestBody categories: List<Category>, @RequestParam(name="p") tourId: Int){

            //Getting the authenticated user
            val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

            //Checking if user has access to modify this tour. If not, this line will throw an exception
            checkModifyAccess(principal, tourId)

            tourService.deleteCategoriesForTour(tourId)
            for(category: Category in categories)
                tourService.addCategoryForTour(category.categoryId!!, tourId)
        }
    }

}