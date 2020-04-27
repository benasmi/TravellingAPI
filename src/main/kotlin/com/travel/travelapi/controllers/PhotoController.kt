package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Photo
import com.travel.travelapi.services.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/photo")
class PhotoController(@Autowired private val photoService: PhotoService) {

    /**
     * Insert photo
     */
    @PostMapping("/insert")
    fun insertPhoto(@RequestBody photos : List<Photo>): List<Int>{
        val inserted = ArrayList<Int>()
        for(p: Photo in photos){
            photoService.insertPhoto(p)
            inserted.add(p.photoId!!)
        }
        return inserted
    }

}