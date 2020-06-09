package com.travel.travelapi.controllers

import com.travel.travelapi.models.TransportFrom
import com.travel.travelapi.services.TransportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class TransportController(@Autowired private val transportService: TransportService) {

    /**
     * Get transport associated with local place in a tour
     * @param id of place tour day
     * @return transport
     */
    fun getLocalPlaceTransportFrom(id: Int): ArrayList<TransportFrom>{
        return transportService.getLocalPlaceTransportFrom(id)
    }

    /**
     * Get transport associated with api place in a tour
     * @param id of api place tour day
     * @return transport
     */
    fun getApiPlaceTransportFrom(id: Int): ArrayList<TransportFrom>{
        return transportService.getApiPlaceTransportFrom(id)
    }
}