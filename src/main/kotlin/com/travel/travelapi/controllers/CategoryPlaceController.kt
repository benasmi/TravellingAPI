package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.services.CategoryPlaceService
import com.travel.travelapi.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/categoryplace")
@RestController
class CategoryPlaceController(@Autowired private val categoryPlaceService: CategoryPlaceService,
@Autowired private val placeService: PlaceService,
                              @Autowired private val authController: AuthController){


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
        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(c: CategoryPlace in categoryPlace){
            //Checking if user is authorized to modify this place. This line will throw an exception if it's not the case
            checkModifyAccess(principal, c.fk_placeId)
            //Inserting category for this place
            categoryPlaceService.insertCategoryForPlace(c)
        }
    }

    /**
     * Checks if user has modify access for the given place's categories
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, placeId: Int){
        //Getting the place user wishes to add category for
        val place = placeService.selectById(placeId)

        //Checking if user has unrestricted access to modify categories for any place
        if(user.hasAuthority("categoryplace:modify_unrestricted")){
            return
        //If user is not authorized to modify categories for any place, we check if the user has created this place, has not published it and admin has not verified it, and has relevant permission
        }else if(user.id!! == (place.userId?.toLong() ?: -1)
                && !place.isVerified!!
                && !place.isPublic!!
                && user.hasAuthority("categoryplace:modify")){
            return
        }else throw UnauthorizedException("User does not have permission to add categories for this place") //Denying access to user
    }

    /**
     * Map category to a place
     */
    @RequestMapping("/delete")
    fun deleteCategory(@RequestBody categoryPlace: List<CategoryPlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(c: CategoryPlace in categoryPlace) {
            //Checking if user is authorized to modify this place's categories. This line will throw an exception if that is not the case
            checkModifyAccess(principal, c.fk_placeId)

            //Deleting category from this place
            categoryPlaceService.deleteCategoryForPlace(c)
        }
    }

    /**
     * Update place categories
     */
    @RequestMapping("/update")
    fun updateTagForPlace(@RequestBody catPlaces: List<Category>, @RequestParam(name="p") id: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user is authorized to modify this place's categories. This line will throw an exception if that is not the case
        checkModifyAccess(principal, id)

        //Deleting all categories
        categoryPlaceService.deleteCategoryForPlaceById(id)

        val cats = ArrayList<CategoryPlace>()
        for(t: Category in catPlaces) {
            cats.add(CategoryPlace(id, t.categoryId!!))
        }

        //Inserting categories
        insertCategory(cats)
    }

}