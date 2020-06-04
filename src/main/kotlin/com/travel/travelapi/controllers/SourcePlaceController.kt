package com.travel.travelapi.controllers

import com.travel.travelapi.models.Category
import com.travel.travelapi.models.CategoryPlace
import com.travel.travelapi.models.Source
import com.travel.travelapi.models.SourcePlace
import com.travel.travelapi.services.SourcePlaceService
import com.travel.travelapi.services.SourceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/sourceplace")
class SourcePlaceController(@Autowired private val sourcePlaceService: SourcePlaceService) {


    /**
     * @param placeId of a place
     * @return all sources for specified place
     */
    fun getSourcesById(placeId: Int): List<Source>{
        return sourcePlaceService.getPlaceSourcesById(placeId)
    }

    /**
     * Map source to a place
     */
    @RequestMapping("/insert")
    fun insertSource(@RequestBody categoryPlace: List<SourcePlace>){
        for(c: SourcePlace in categoryPlace){
            sourcePlaceService.insertSource(c)
        }
    }

    /**
     * Delete source from a place
     */
    @RequestMapping("/delete")
    fun deleteSource(@RequestBody categoryPlace: List<SourcePlace>){
        for(c: SourcePlace in categoryPlace){
            sourcePlaceService.deleteSource(c)
        }
    }

    /**
     * Update place sources
     */
    @RequestMapping("/update")
    fun updateTagForPlace(@RequestBody catPlaces: List<Source>, @RequestParam(name="p") id: Int){
        sourcePlaceService.deleteSourcesById(id)

        val cats = ArrayList<SourcePlace>()
        for(t: Source in catPlaces)
            cats.add(SourcePlace(t.sourceId!!, id))

        insertSource(cats)
    }
}