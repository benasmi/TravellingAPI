package com.travel.travelapi.controllers

import com.travel.travelapi.models.PlaceLocal
import com.travel.travelapi.services.*
import com.travel.travelapi.sphinx.SphinxService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/place")
class PlaceController(@Autowired private val placeService: PlaceService,
                      @Autowired private val categoryController: CategoryPlaceController,
                      @Autowired private val workingScheduleService: WorkingScheduleController,
                      @Autowired private val parkingPlaceController: ParkingPlaceController,
                      @Autowired private val reviewService: ReviewController,
                      @Autowired private val photoPlaceController: PhotoPlaceController,
                      @Autowired private val tagPlaceController: TagPlaceController,
                      @Autowired private val sphinxService: SphinxService){


    /**
     * @return all places that match given query
     * @param full if given true returns places mapped with categories, parking, reviews, schedule otherwise just general info
     * @param keyword of a place
     */
    @GetMapping("/search")
    fun getPlaces(@RequestParam full: Boolean = false, @RequestParam keyword: String): List<PlaceLocal> {
        val ids = sphinxService.searchPlacesByKeyword(keyword)
        val places: List<PlaceLocal> = placeService.selectAll(ids.joinToString(","))
            if(full){
                for (value: PlaceLocal in places) {
                    value.categories = categoryController.getCategoriesById(value.placeId!!)
                    value.parking = parkingPlaceController.getParkingLocationsById(value.placeId)
                    value.reviews = reviewService.getReviewsById(value.placeId)
                    value.schedule = workingScheduleService.getWorkingScheduleById(value.placeId)
                    value.photos = photoPlaceController.getPhotosById(value.placeId)
                    value.tags = tagPlaceController.getTagsById(value.placeId)
                }
            }
        return places
    }



}