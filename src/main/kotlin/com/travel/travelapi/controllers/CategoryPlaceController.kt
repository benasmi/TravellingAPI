package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.models.Tag
import com.travel.travelapi.models.TagPlace
import com.travel.travelapi.services.CategoryPlaceService
import com.travel.travelapi.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/categoryplace")
@RestController
class CategoryPlaceController(@Autowired private val categoryPlaceService: CategoryPlaceService){


    /**
     * @param placeId of a place
     * @return all categories for specified place
     */
    fun getCategoriesById(placeId: Int): List<Category>{
        return categoryPlaceService.selectByPlaceId(placeId)
    }

    /**
     * Map category to a place
     */
    @RequestMapping("/insert")
    fun insertCategory(@RequestBody categoryPlace: List<CategoryPlace>){
        for(c: CategoryPlace in categoryPlace){
            categoryPlaceService.insertCategoryForPlace(c)
        }
    }

    /**
     * Map category to a place
     */
    @RequestMapping("/delete")
    fun deleteCategory(@RequestBody categoryPlace: List<CategoryPlace>){
        for(c: CategoryPlace in categoryPlace)
            categoryPlaceService.deleteCategoryForPlace(c)
    }

    /**
     * Update place categories
     */
    @RequestMapping("/update")
    fun updateTagForPlace(@RequestBody catPlaces: List<Category>, @RequestParam(name="p") id: Int){
        categoryPlaceService.deleteCategoryForPlaceById(id)

        val cats = ArrayList<CategoryPlace>()
        for(t: Category in catPlaces)
            cats.add(CategoryPlace(id, t.categoryId!!))

        insertCategory(cats)
    }

}