package com.travel.travelapi.controllers

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.models.PlaceLocal
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.sphinx.SphinxService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


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
     * @param p is page number
     * @param s is page size
     *
     * If @param p and @param s are not present, default values are p=1 and s=10
     */
    @GetMapping("/search")
    fun getPlaces(@RequestParam(required = false) full: Boolean = false,
                  @RequestParam(required = true) keyword: String,
                  @RequestParam(required = false, defaultValue = "1") p: Int,
                  @RequestParam(required = false, defaultValue = "10") s: Int): PageInfo<PlaceLocal> {
        val ids = sphinxService.searchPlacesByKeyword(keyword)
        PageHelper.startPage<PlaceLocal>(p,s)
        //val t = ids.joinToString(",").
        val places: Page<PlaceLocal> = placeService.selectAll(ids.joinToString(","))
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

        return PageInfo(places)
    }



}