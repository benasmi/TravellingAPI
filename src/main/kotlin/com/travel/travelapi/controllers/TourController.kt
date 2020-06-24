package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.facebook.places.api.ApiPlaceController
import com.travel.travelapi.models.*
import com.travel.travelapi.services.TourService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/tour")
class TourController(@Autowired private val tourService: TourService,
                     @Autowired private val tourDayController: TourDayController,
                     @Autowired private val placeController: PlaceController,
                     @Autowired private val apiPlaceController: ApiPlaceController) {


    /**
     * @param keyword of a place
     * @param p is page number
     * @param s is page size
     * @return paged tour data for admin
     */
    @GetMapping("/searchadmin")
    fun getPlacesAdmin(@RequestParam(required = true, defaultValue = "", name = "keyword") keyword: String,
                       @RequestParam(required = false, defaultValue = "1", name = "p") p: Int,
                       @RequestParam(required = false, defaultValue = "10", name = "s") s: Int,
                       @RequestParam("o", defaultValue = "", name = "o") o: String ): PageInfo<Tour> {

        PageHelper.startPage<Tour>(p, s)
        val filterOptions = o.split(",")
        val tours = tourService.selectAllAdmin(keyword,filterOptions)
        return PageInfo(tours)
    }
    /**
     * Get tour by id
     * @param id of a tour
     * @return full tour information with days and places
     */
    @GetMapping("")
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
        tourService.deleteTourById(id)
    }

    /**
     * Insert new tour
     * @param tour
     */
    @PostMapping("/insert")
    fun insertTour(@RequestBody tour: Tour): Int{
        tourService.insertTour(tour)
        return tour.tourId!!
    }

}