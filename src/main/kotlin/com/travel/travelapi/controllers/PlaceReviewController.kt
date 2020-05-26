package com.travel.travelapi.controllers

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.models.Place
import com.travel.travelapi.models.PlaceLocal
import com.travel.travelapi.models.PlaceReview
import com.travel.travelapi.models.PlaceReviewInfo
import com.travel.travelapi.services.PlaceReviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reviews")
class PlaceReviewController(@Autowired private val placeReviewService: PlaceReviewService) {

    @GetMapping("/all")
    fun getPagedReviews(@RequestParam("p") id: Int,
                        @RequestParam(required = false, defaultValue = "1", name = "page") p: Int,
                        @RequestParam(required = false, defaultValue = "5", name = "size") s: Int,
                        @RequestParam(required = false, defaultValue = "newest", name = "o") order: String)
            : PageInfo<PlaceReviewInfo> {

        PageHelper.startPage<PlaceReviewInfo>(p, s)
        val placeReviewInfo: Page<PlaceReviewInfo> = placeReviewService.getReviewsById(id,order)

        return PageInfo(placeReviewInfo)
    }
}