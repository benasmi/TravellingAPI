package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.services.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController


@RestController
class PhotoController(@Autowired private val photoService: PhotoService) {


}