package com.travel.travelapi.controllers

import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.*
import com.travel.travelapi.services.ExplorePageService
import com.travel.travelapi.services.PhotoPlaceService
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.TourService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/explore")
class ExplorePageController(
        @Autowired private val explorePageService: ExplorePageService,
        @Autowired private val recommendationController: RecommendationController,
        @Autowired private val photoPlaceService: PhotoPlaceService,
        @Autowired private val placeService: PlaceService,
        @Autowired private val tourController: TourController

){

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('explore:update')")
    fun update(@RequestBody recommendations: List<Int>){
        if(recommendations.distinct().count() == recommendations.count()){
            //No duplicate recommendations
            explorePageService.update(recommendations)
        }else{
            throw InvalidParamsException("The explore page cannot have any duplicate recommendations")
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('explore:read')")
    fun getExplorePage(
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int
    ): PageInfo<Recommendation>{
        PageHelper.startPage<Recommendation>(p, s)

        val recommendations = explorePageService.selectAll()

        for(recommendation in recommendations)
            recommendationController.extendRecommendation(recommendation)

        return PageInfo(recommendations)
    }


    @GetMapping("/search")
    fun search(@RequestParam keyword: String): ClientSearchResult {
        val places = placeService.searchClient(keyword)
        val tours = arrayListOf<Tour>()
        for(place in places){
            val photos = photoPlaceService.selectPhotosById(place.placeId!!)
            place.photos = if(photos.count() > 0) arrayListOf(photos[0]) else arrayListOf()

            //Fetching tours associated with the place
            val tourIds = explorePageService.selectToursForPlace(place.placeId)
            for(tour in tourIds)
                tours.add(tourController.tourOverviewById(tour.tourId!!))
        }


        return ClientSearchResult(places, tours, explorePageService.matchSearch(keyword))
    }

}