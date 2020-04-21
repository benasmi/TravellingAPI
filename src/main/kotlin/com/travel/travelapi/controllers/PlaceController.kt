package com.travel.travelapi.controllers

import com.travel.travelapi.models.Place
import com.travel.travelapi.pojos.Parking
import com.travel.travelapi.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/place")
class PlaceController(@Autowired private val service: PlaceService){


    @GetMapping("/all")
    fun parking(): List<Place> {
        return service.selectAll()
    }

}