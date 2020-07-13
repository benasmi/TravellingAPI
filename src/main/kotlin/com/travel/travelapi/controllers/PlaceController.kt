package com.travel.travelapi.controllers

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.Parking
import com.travel.travelapi.models.Photo
import com.travel.travelapi.models.PlaceLocal
import com.travel.travelapi.services.PlaceReviewService
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.SourcePlaceService
import com.travel.travelapi.sphinx.SphinxService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import kotlin.math.absoluteValue


@RestController
@RequestMapping("/place")
class PlaceController(@Autowired private val placeService: PlaceService,
                      @Autowired private val categoryController: CategoryPlaceController,
                      @Autowired private val workingScheduleService: WorkingScheduleController,
                      @Autowired private val parkingPlaceController: ParkingPlaceController,
                      @Autowired private val placeReviewService: PlaceReviewService,
                      @Autowired private val photoPlaceController: PhotoPlaceController,
                      @Autowired private val tagPlaceController: TagPlaceController,
                      @Autowired private val sourcePlaceController: SourcePlaceController,
                      @Autowired private val sphinxService: SphinxService){




    /**
     * @return all places that match given query
     * @param full if given true returns places mapped with categories, parking, reviews, schedule otherwise just general info
     * @param keyword of a place
     * @param p is page number
     * @param s is page size
     *
     * If @param p and @param s are not present, default values are p=1 and s=10
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('place:read')")
    fun getPlaces(@RequestParam(required = false) full: Boolean = false,
                  @RequestParam(required = true, defaultValue = "") keyword: String,
                  @RequestParam(required = false, defaultValue = "1") p: Int,
                  @RequestParam(required = false, defaultValue = "10") s: Int): PageInfo<PlaceLocal> {

        val places: Page<PlaceLocal>
        PageHelper.startPage<PlaceLocal>(p, s)
        places = if(keyword == ""){
            placeService.selectAll()
        }else{
            val ids = sphinxService.searchPlacesByKeyword(keyword)
            if(ids.isNotEmpty())
                placeService.search(ids)
            else Page()
        }
           extendPlace(full,places)
        return PageInfo(places)
    }

    /**
     * @return all places that match given query for admin
     * @param full if given true returns places mapped with categories, parking, reviews, schedule otherwise just general info
     * @param keyword of a place
     * @param p is page number
     * @param s is page size
     * @param location of a place
     * @param range of places nearby
     *
     * If @param p and @param s are not present, default values are p=1 and s=10
     */
    @GetMapping("/searchadmin")
    @PreAuthorize("hasAuthority('place:read_unrestricted')")
    fun getPlacesAdmin(@RequestParam(required = false,name = "full") full: Boolean = false,
                       @RequestParam(required = true, defaultValue = "", name = "keyword") keyword: String,
                       @RequestParam(required = false, defaultValue = "1", name = "p") p: Int,
                       @RequestParam(required = false, defaultValue = "10", name = "s") s: Int,
                       @RequestParam(defaultValue = "", name = "o") o: String,
                       @RequestParam(defaultValue = "", name = "c") c: String,
                       @RequestParam(defaultValue = "", name = "di") di: String,
                       @RequestParam(defaultValue = "", name = "dm") dm: String,
                       @RequestParam(defaultValue = "", name = "countries") countries: String,
                       @RequestParam(defaultValue = "", name = "cities") cities: String,
                       @RequestParam(defaultValue = "", name = "municipalities") municipalities: String,
                       @RequestParam(defaultValue = "", name = "l") location: String,
                       @RequestParam(defaultValue = "50", name = "range") range: String): PageInfo<PlaceLocal> {


        PageHelper.startPage<PlaceLocal>(p, s)
        val places = placeService.selectAllAdmin(keyword,
                if(o.isNotEmpty()) o.split(",") else ArrayList(),
                if(c.isNotEmpty()) c.split(',') else ArrayList(),
                if(di.isNotEmpty()) di.split(',') else ArrayList(),
                if(dm.isNotEmpty()) dm.split(',') else ArrayList(),
                if(countries.isNotEmpty()) countries.split(',') else ArrayList(),
                if(cities.isNotEmpty()) cities.split(',') else ArrayList(),
                if(municipalities.isNotEmpty()) municipalities.split(',') else ArrayList(),
                if(location.isNotEmpty()) location.split(',') else ArrayList(),
                range.toDouble())
        extendPlace(full, places)
        return PageInfo(places)
    }


    @PostMapping("/update")
    fun updatePlace(@RequestBody p: PlaceLocal){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to modify this place's basic info. If not, this line will throw an exception
        checkModifyAccess(principal, p.placeId!!)

        //Updating the place
        placeService.updatePlace(p)
    }

    /**
     * Checks if user has permission to modify the place with a given ID.
     * Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, placeId: Int){
        //Getting the relevant place from database
        val place = placeService.selectById(placeId)

        //Checking if user has permission to modify place info unconditionally
        if(user.hasAuthority("place:modify_unrestricted")){
            return
            //Else we check if user has authority to modify their place's basic info and the given place is created by them
        }else if(place.userId != null
                && place.userId!!.toLong() == user.id
                && !place.isVerified!!
                && !place.isPublic!!
                && user.hasAuthority("place:modify"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }

    /**
     * @return place by id
     * @param full if given true returns places mapped with categories, parking, reviews, schedule otherwise just general info
     * @param keyword of a place
     * @param p is page number
     * @param s is page size
     *
     * If @param p and @param s are not present, default values are p=1 and s=10
     */
    @GetMapping("/getplace")
    @PreAuthorize("hasAuthority('place:read')")
    fun getPlaceById(@RequestParam(required = false) full: Boolean = false,
                  @RequestParam(name="p") id: Int): PlaceLocal {

        val place = placeService.selectById(id)
        if(full){
            place.categories = categoryController.getCategoriesById(id)
            place.parking = parkingPlaceController.getParkingLocationsById(id)
            place.schedule = workingScheduleService.getWorkingSchedulesById(id)
            place.totalReviews = placeReviewService.getReviewsCountsByPlace(id)
            place.overallStarRating = placeReviewService.getReviewsAverageRating(id)
            place.photos = photoPlaceController.getPhotosById(id)
            place.tags = tagPlaceController.getTagsById(id)
            place.sources = sourcePlaceController.getSourcesById(id)
        }
        place.photos = photoPlaceController.getPhotosById(id)
        return place
    }

    @GetMapping("/delete")
    fun deletePlace(@RequestParam("p") id: Int){
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to remove this place. If not, this line will throw an exception
        checkModifyAccess(principal, id)

        //Deleting the place
        placeService.deletePlace(id)
    }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('place:insert')")
    fun insertPlace(@RequestBody p: PlaceLocal): Int{
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        p.userId = principal.id!!.toInt()
        placeService.insertPlace(p)
        return p.placeId!!;
    }

    private fun extendPlace(full: Boolean, places: Page<PlaceLocal>) {
            for (value: PlaceLocal in places) {
             value.photos = photoPlaceController.getPhotosById(value.placeId!!)
                if(full){
                    value.categories = categoryController.getCategoriesById(value.placeId!!)
                    value.parking = parkingPlaceController.getParkingLocationsById(value.placeId)
                    value.schedule = workingScheduleService.getWorkingSchedulesById(value.placeId)
                    value.totalReviews = placeReviewService.getReviewsCountsByPlace(value.placeId)
                    value.overallStarRating = placeReviewService.getReviewsAverageRating(value.placeId)
                    value.tags = tagPlaceController.getTagsById(value.placeId)
                    value.sources = sourcePlaceController.getSourcesById(value.placeId)
                }
            }
    }


    @GetMapping("/country/all")
    @PreAuthorize("hasAuthority('place:read')")
    fun getAllCountries(): ArrayList<String>{
        return placeService.getAllCountries()
    }

    @GetMapping("/municipality/all")
    @PreAuthorize("hasAuthority('place:read')")
    fun getAllMunicipalities(@RequestParam(name="countryRestrictions", defaultValue = "") countryRestrictions: String): ArrayList<String>{
        return placeService.getAllMunicipalities(if(countryRestrictions.isNotEmpty()) countryRestrictions.split(',') else ArrayList())
    }

    @GetMapping("/city/all")
    @PreAuthorize("hasAuthority('place:read')")
    fun getAllCities(@RequestParam(name="countryRestrictions", defaultValue = "") countryRestrictions: String,
                             @RequestParam(name="munRestrictions", defaultValue = "") munRestrictions: String): ArrayList<String>{
        return placeService.getAllCities(
                if(countryRestrictions.isNotEmpty()) countryRestrictions.split(',') else ArrayList(),
                if(munRestrictions.isNotEmpty()) munRestrictions.split(',') else ArrayList())
    }

    @GetMapping("/county/all")
    @PreAuthorize("hasAuthority('place:read')")
    fun getAllCounties(): ArrayList<String>{
        return placeService.getAllCounties()
    }


}
