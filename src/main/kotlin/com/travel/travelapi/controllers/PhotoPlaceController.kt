package com.travel.travelapi.controllers

import com.travel.travelapi.models.*
import com.travel.travelapi.services.PhotoPlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
     * Delete photo from place
     */
    @RequestMapping("/delete")
    fun deletePhotoFromPlace(@RequestBody photos: List<PhotoPlace>){
        for(p: PhotoPlace in photos){
            photoPlaceService.deletePhotoFromPlace(p)
        }
    }

    /**
     * Update place categories
     */
    @RequestMapping("/update")
    fun updateTagForPlace(@RequestBody photoPlaces: List<Photo>, @RequestParam(name="p") id: Int){
        photoPlaceService.deletePhotoForPlaceById(id)

        val photos = ArrayList<PhotoPlace>()
        photoPlaces.forEachIndexed { index, p ->
            photos.add(PhotoPlace(p.photoId,id,index))
        }

        insertPhotoForPlace(photos)
    }

}