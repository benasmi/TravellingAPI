package com.travel.travelapi.controllers

import com.travel.travelapi.services.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/place")
class PlaceController(){


    @GetMapping("/all")
    fun test(): String{
        return "s"
    }


}