package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.*
import com.travel.travelapi.services.PhotoPlaceService
import com.travel.travelapi.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/photoplace")
@RestController
class PhotoPlaceController (@Autowired private val photoPlaceService: PhotoPlaceService,
@Autowired private val placeService: PlaceService){

    /**
     * @param placeId of a place
     * @return all photos for specified place
     */
    fun getPhotosById(placeId: Int): List<Photo>{
        return photoPlaceService.selectPhotosById(placeId)
    }

    /**
     * Map photo to a place
     */
    @RequestMapping("/insert")
    fun insertPhotoForPlace(@RequestBody photos: List<PhotoPlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(p: PhotoPlace in photos){

            //Checking if user has access to modify this place's photos. If not, this line will throw an exception
            checkModifyAccess(principal, p.fk_placeId!!)

            //Inserting photo for this place
            photoPlaceService.insertPhotoForPlace(p)
        }
    }

    /**
     * Checks if user has permission to modify the photos for a given place with the given place ID.
     * Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, placeId: Int){
        //Getting the relevant place from database
        val place = placeService.selectById(placeId)

        //Checking if user has permission to modify all place photos unconditionally
        if(user.hasAuthority("photoplace:modify_unrestricted")){
            return
            //Else we check if user has authority to modify their place's photos and the given place is created by them
        }else if(place.userId != null
                && place.userId!!.toLong() == user.id
                && !place.isVerified!!
                && !place.isPublic!!
                && user.hasAuthority("photoplace:modify"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }

    /**
     * Delete photo from place
     */
    @RequestMapping("/delete")
    fun deletePhotoFromPlace(@RequestBody photos: List<PhotoPlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(p: PhotoPlace in photos){

            //Checking if user has access to modify this place's photos. If not, this line will throw an exception
            checkModifyAccess(principal, p.fk_placeId!!)

            //Dereferencing photo from a place
            photoPlaceService.deletePhotoFromPlace(p)
        }
    }

    /**
     * Update place categories
     */
    @RequestMapping("/update")
    fun updatePhotosForPlace(@RequestBody photoPlaces: List<Photo>, @RequestParam(name="p") id: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to modify this place's photos. If not, this line will throw an exception
        checkModifyAccess(principal, id)

        photoPlaceService.deletePhotoForPlaceById(id)

        val photos = ArrayList<PhotoPlace>()
        photoPlaces.forEachIndexed { index, p ->
            photos.add(PhotoPlace(p.photoId,id,index))
        }

        insertPhotoForPlace(photos)
    }

}