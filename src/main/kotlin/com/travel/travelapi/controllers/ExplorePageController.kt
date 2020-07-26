package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.Recommendation
import com.travel.travelapi.services.ExplorePageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/explore")
class ExplorePageController(
        @Autowired private val explorePageService: ExplorePageService,
        @Autowired private val recommendationController: RecommendationController
){

    @PostMapping("/update")
    fun update(@RequestBody recommendations: List<Int>){
        if(recommendations.distinct().count() == recommendations.count()){
            //No duplicate recommendations
            explorePageService.update(recommendations)
        }else{
            throw InvalidParamsException("The explore page cannot have any duplicate recommendations")
        }
    }

    @GetMapping("/")
    fun getExplorePage(
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int
    ): PageInfo<Recommendation>{
        PageHelper.startPage<Recommendation>(p, s)

        val recommendationIds = explorePageService.selectAll()
//        val recommendations = arrayListOf<Recommendation>()
//
//        for(recommendation in recommendationIds)
//            recommendations.add(recommendationController.getRecommendationById(recommendation.id!!))

        return PageInfo(recommendationIds)
    }

}