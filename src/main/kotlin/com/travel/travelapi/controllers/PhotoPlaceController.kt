package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Photo
import com.travel.travelapi.models.PhotoPlace
import com.travel.travelapi.models.TagPlace
import com.travel.travelapi.services.PhotoPlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/photoplace")
@RestController
class PhotoPlaceController (@Autowired private val photoPlaceService: PhotoPlaceService){

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
        for(p: PhotoPlace in photos){
            photoPlaceService.insertPhotoForPlace(p)
        }
    }

    /**
     * Update photo priority
     */
    @RequestMapping("/update")
    fun updatePhotoIndexesForPlace(@RequestBody photos: List<PhotoPlace>){
        for(p: PhotoPlace in photos){
            photoPlaceService.updatePhotoIndexing(p)
        }
    }

    /**
     * Delete photo from place
     */
    @RequestMapping("/delete")
    fun deletePhotoFromPlace(@RequestBody photos: List<PhotoPlace>){
        for(p: PhotoPlace in photos){
            photoPlaceService.deletePhotoFromPlace(p)
        }
    }

}