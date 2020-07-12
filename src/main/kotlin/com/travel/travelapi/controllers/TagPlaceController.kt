package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.Tag
import com.travel.travelapi.models.TagPlace
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.TagPlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tagsplace")
class TagPlaceController (@Autowired private val tagPlaceService: TagPlaceService,
        @Autowired private val placeService: PlaceService){

    /**
     * Checks if user has modify access for the given place's tags
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, placeId: Int){
        //Getting the place user wishes to add tag for
        val place = placeService.selectById(placeId)

        //Checking if user has unrestricted access to modify tags for any place
        if(user.hasAuthority("tagplace:modify_unrestricted")){
            return
            //If user is not authorized to modify tags for any place, we check if the user has created this place, has not published it and admin has not verified it, and has relevant permission
        }else if(user.id!! == (place.userId?.toLong() ?: -1)
                && !place.isVerified!!
                && !place.isPublic!!
                && user.hasAuthority("tagplace:modify")){
            return
        }else throw UnauthorizedException("User does not have permission to add categories for this place") //Denying access to user
    }

    /**
     * Get tags for a place
     * @param placeId of a place
     */
    fun getTagsById(placeId: Int) : List<Tag>{
        return tagPlaceService.selectTagsById(placeId)
    }

    /**
     * Map tags to a place
     */
    @RequestMapping("/insert")
    fun insertTagForPlace(@RequestBody tagPlaces: List<TagPlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(t: TagPlace in tagPlaces) {
            //Checking if user is authorized to modify this place. This line will throw an exception if it's not the case
            checkModifyAccess(principal, t.fk_placeId!!)

            //Inserting tag for place
            tagPlaceService.insertTagForPlace(t)
        }
    }

    /**
     * Update place tags
     */
    @RequestMapping("/update")
    fun updateTagsForPlace(@RequestBody tagPlaces: List<Tag>, @RequestParam(name="p") id: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user is authorized to modify this place. This line will throw an exception if it's not the case
        checkModifyAccess(principal, id)

        tagPlaceService.deleteTagForPlaceById(id)

        val tags = ArrayList<TagPlace>()
        for(t: Tag in tagPlaces)
            tags.add(TagPlace(t.tagId,id))

        insertTagForPlace(tags)

    }

    @PostMapping("/delete")
    fun deleteTagForPlace(@RequestBody tagPlaces: List<TagPlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(t: TagPlace in tagPlaces) {

            //Checking if user is authorized to modify this place. This line will throw an exception if it's not the case
            checkModifyAccess(principal, t.fk_placeId!!)

            //Deleting tag for place
            tagPlaceService.deleteTagForPlace(t)
        }
    }


}