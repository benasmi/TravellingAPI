package com.travel.travelapi.controllers

import com.travel.travelapi.models.*
import com.travel.travelapi.services.*
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
                      @Autowired private val tagPlaceController: TagPlaceController){


    /**
     * @return all places
     * @param full if given true returns places mapped with categories, parking, reviews, schedule otherwise just general info
     *
     */
    @GetMapping("/all")
    fun getPlaces(@RequestParam full: Boolean = false): List<Place> {
        val places: List<Place> = placeService.selectAll()
            if(full){
                for (value: Place in places) {
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

    /**
     *
     */
    @PostMapping("/insert")
    fun insertPlaces(@RequestBody places: List<Place>): List<Int>{
        val inserted = ArrayList<Int>()
        for(p: Place in places){
            placeService.insertPlace(p)
            inserted.add(p.placeId!!)
        }
        return inserted
    }
}