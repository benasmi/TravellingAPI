package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.services.CategoryPlaceService
import com.travel.travelapi.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController


@RestController
class CategoryPlaceController(@Autowired private val categoryPlaceService: CategoryPlaceService){


    /**
     * @param placeId of a place
     * @return all categories for specified place
     */
    fun getCategoriesById(placeId: Int): List<Category>{
        return categoryPlaceService.selectByPlaceId(placeId)
    }

}