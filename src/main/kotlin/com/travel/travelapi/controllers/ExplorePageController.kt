package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.Recommendation
import com.travel.travelapi.services.ExplorePageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/explore")
class ExplorePageController(
        @Autowired private val explorePageService: ExplorePageService,
        @Autowired private val recommendationController: RecommendationController
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

}