package com.travel.travelapi.controllers

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.models.PlaceLocal
import com.travel.travelapi.models.Recommendation
import com.travel.travelapi.models.RecommendationType
import com.travel.travelapi.services.AuthService
import com.travel.travelapi.services.RecommendationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/recommendation")
class RecommendationController(
        @Autowired private val recommendationService: RecommendationService,
        @Autowired private val placeController: PlaceController
) {

    // TODO: add requires permission
    @PostMapping("/create")
    fun createRecommendation(@RequestBody recommendation: Recommendation): Int {
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        recommendation.user = principal.id!!.toInt()

        recommendationService.createRecommendation(recommendation)

        return recommendation.id!!
    }

    @GetMapping("/search")
    fun searchRecommendations(@RequestParam("keyword", defaultValue = "") keyword: String?,
                              @RequestParam("full", defaultValue = "false") full: Boolean?,
                              @RequestParam(required = false, defaultValue = "1") p: Int,
                              @RequestParam(required = false, defaultValue = "10") s: Int): PageInfo<Recommendation> {
        PageHelper.startPage<Recommendation>(p, s)

        val recommendationsFound = recommendationService.searchRecommendations(keyword)
        if (!full!!)
            return PageInfo(recommendationsFound)

        for (recommendation in recommendationsFound) {
            if (recommendation.type == RecommendationType.PLACE.id) {
                val placeIds = recommendationService.selectPlacesForRecommendation(recommendation.id!!)
                val places = arrayListOf<PlaceLocal>()
                for (placeId in placeIds)
                    places.add(placeController.getPlaceById(false, placeId))
                recommendation.places = places
            } else if (recommendation.type == RecommendationType.TOUR.id) {
                //recommendation.tours = recommendationService.selectToursForRecommendation(recommendation.id!!)
                // TODO
            }
        }
        return PageInfo(recommendationsFound)
    }

    data class ObjectRecommendation(val id: Int, val recommendationId: Int, val type: Int)

    @PostMapping("/addObject")
    fun addPlace(@RequestBody data: ObjectRecommendation){
        if(data.type == RecommendationType.PLACE.id){
            recommendationService.addPlace(data)
        }else if(data.type == RecommendationType.TOUR.id){
            recommendationService.addTour(data)
        }
    }

    @PostMapping("/removeObject")
    fun removePlace(@RequestBody data: ObjectRecommendation){
        if(data.type == RecommendationType.PLACE.id){
            recommendationService.removePlace(data)
        }else if(data.type == RecommendationType.TOUR.id){
            recommendationService.removeTour(data)
        }
    }

    @PostMapping("/update")
    fun updateRecommendation(@RequestBody data: Recommendation){
        recommendationService.updateRecommendation(data)
    }

}