package com.travel.travelapi.controllers

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.models.PlaceLocal
import com.travel.travelapi.services.PlaceReviewService
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.SourcePlaceService
import com.travel.travelapi.sphinx.SphinxService
import org.springframework.beans.factory.annotation.Autowired
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
     *
     * If @param p and @param s are not present, default values are p=1 and s=10
     */
    @GetMapping("/searchadmin")
    fun getPlacesAdmin(@RequestParam(required = false,name = "full") full: Boolean = false,
                  @RequestParam(required = true, defaultValue = "", name = "keyword") keyword: String,
                  @RequestParam(required = false, defaultValue = "1", name = "p") p: Int,
                  @RequestParam(required = false, defaultValue = "10", name = "s") s: Int,
                  @RequestParam(defaultValue = "", name = "o") o: String,
                  @RequestParam(defaultValue = "", name = "c") c: String,
                  @RequestParam(defaultValue = "", name = "di") di: String,
                  @RequestParam(defaultValue = "", name = "dm") dm: String): PageInfo<PlaceLocal> {


        PageHelper.startPage<PlaceLocal>(p, s)
        val places = placeService.selectAllAdmin(keyword,
                if(o.isNotEmpty()) o.split(",") else ArrayList(),
                if(c.isNotEmpty()) c.split(',') else ArrayList(),
                if(di.isNotEmpty()) di.split(',') else ArrayList(),
                if(dm.isNotEmpty()) dm.split(',') else ArrayList())
        extendPlace(full, places)
        return PageInfo(places)
    }


    @PostMapping("/update")
    fun updatePlace(@RequestBody p: PlaceLocal){
        placeService.updatePlace(p)
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
        placeService.deletePlace(id)
    }

    @PostMapping("/insert")
    fun insertPlace(@RequestBody p: PlaceLocal): Int{
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
}