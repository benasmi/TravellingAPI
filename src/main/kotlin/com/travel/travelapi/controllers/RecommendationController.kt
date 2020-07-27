package com.travel.travelapi.controllers

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.*
import com.travel.travelapi.services.AuthService
import com.travel.travelapi.services.RecommendationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.sql.SQLException

@RestController
@RequestMapping("/recommendation")
class RecommendationController(
        @Autowired private val recommendationService: RecommendationService,
        @Autowired private val placeController: PlaceController,
        @Autowired private val tourController: TourController
) {

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('recommendation:write')")
    fun createRecommendation(@RequestBody recommendation: Recommendation): Int {
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        recommendation.user = principal.id!!.toInt()

        recommendationService.createRecommendation(recommendation)

        return recommendation.id!!
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('recommendation:read')")
    fun searchRecommendations(@RequestParam("keyword", defaultValue = "") keyword: String?,
                              @RequestParam("full", defaultValue = "false") full: Boolean?,
                              @RequestParam("type") type: Int?,
                              @RequestParam(required = false, defaultValue = "1") p: Int,
                              @RequestParam(required = false, defaultValue = "10") s: Int): PageInfo<Recommendation> {
        PageHelper.startPage<Recommendation>(p, s)

        val recommendationsFound = recommendationService.searchRecommendations(keyword, type)
        if (!full!!)
            return PageInfo(recommendationsFound)

        for (recommendation in recommendationsFound) {
            extendRecommendation(recommendation)
        }
        return PageInfo(recommendationsFound)
    }

    fun extendRecommendation(recommendation: Recommendation){
        if (recommendation.type == RecommendationType.PLACE.id) {
            val placeIds = recommendationService.selectPlacesForRecommendation(recommendation.id!!)
            val places = arrayListOf<RecommendationPlaces>()
            for (placeId in placeIds)
                places.add(RecommendationPlaces.createFromPlaceInstance(placeController.getPlaceById(false, placeId)))
            recommendation.objects = places
        } else if (recommendation.type == RecommendationType.TOUR.id) {
            val tourIds = recommendationService.selectToursForRecommendation(recommendation.id!!)
            val tours = arrayListOf<RecommendationTours>()
            for (tour in tourIds)
                tours.add(RecommendationTours.createFromTourInstance(tourController.tourOverviewById(tour.tourId!!)))
            recommendation.objects = tours
        }
    }

    data class ObjectRecommendation(val id: Int, val recommendationId: Int)

    fun getRecommendationById(id: Int): Recommendation{
        val recommendation: Recommendation
        try{
            recommendation = recommendationService.selectRecommendationById(id)
        }catch(e: SQLException) {
            throw InvalidParamsException("Recommendation id is not valid")
        }
        return recommendation
    }

    @PostMapping("/addObject")
    @PreAuthorize("hasAuthority('recommendation:write')")
    fun addPlace(@RequestBody data: ObjectRecommendation){
        val recommendation = getRecommendationById(data.recommendationId)
        if(recommendation.type == RecommendationType.PLACE.id){
            if(recommendationService.placeExists(data.recommendationId, data.id))
                throw InvalidParamsException("The place you wanted to add is already in the recommendation.")
            recommendationService.addPlace(data)
        }else if(recommendation.type == RecommendationType.TOUR.id){
            if(recommendationService.tourExists(data.recommendationId, data.id))
                throw InvalidParamsException("The tour you wanted to add is already in the recommendation.")
            recommendationService.addTour(data)
        }
    }

    @PostMapping("/removeObject")
    @PreAuthorize("hasAuthority('recommendation:write')")
    fun removePlace(@RequestBody data: ObjectRecommendation){
        val recommendation = getRecommendationById(data.recommendationId)
        if(recommendation.type == RecommendationType.PLACE.id){
            recommendationService.removePlace(data)
        }else if(recommendation.type == RecommendationType.TOUR.id){
            recommendationService.removeTour(data)
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('recommendation:write')")
    fun updateRecommendation(@RequestBody data: Recommendation){
        //Getting the recommendation by ID so we can determine it's type
        val recommendation = getRecommendationById(data.id!!)

        data.type = recommendation.type

        val hasDuplicates = data.objects!!.distinctBy { obj -> obj.id }.count() != data.objects!!.count()

        if(hasDuplicates)
            throw InvalidParamsException("The recommendation contains duplicates!")
        else
            recommendationService.updateRecommendation(data)
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('recommendation:write')")
    fun removeRecommendation(@RequestBody id: Int){
        recommendationService.removeRecommendation(id)
    }
}