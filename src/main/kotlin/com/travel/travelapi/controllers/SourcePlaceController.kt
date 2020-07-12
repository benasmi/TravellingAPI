package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.Source
import com.travel.travelapi.models.SourcePlace
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.SourcePlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/sourceplace")
class SourcePlaceController(@Autowired private val sourcePlaceService: SourcePlaceService,
                            @Autowired private val placeService: PlaceService) {


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

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(c: SourcePlace in categoryPlace){

            //Checking if user has access to modify this place's sources. If not, this line will throw an exception
            checkModifyAccess(principal, c.fk_placeId!!)

            //Inserting source for this place
            sourcePlaceService.insertSource(c)
        }
    }

    /**
     * Delete source from a place
     */
    @RequestMapping("/delete")
    fun deleteSource(@RequestBody sourcePlaces: List<SourcePlace>){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        for(c: SourcePlace in sourcePlaces){

            //Checking if user has access to modify this place's sources. If not, this line will throw an exception
            checkModifyAccess(principal, c.fk_placeId!!)

            sourcePlaceService.deleteSource(c)
        }
    }

    /**
     * Checks if user has permission to modify sources of a place with the given ID.
     * Throws an exception if user is not permitted
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, placeId: Int){

        //Getting the relevant parking from database
        val place = placeService.selectById(placeId)

        //Checking if user has permission to modify all parkings unconditionally
        if(user.hasAuthority("source:modify_unrestricted")){
            return
            //Else we check if user has authority to modify their parking(s) and the given parking is created by them
        }else if(place.userId != null
                && place.userId!!.toLong() == user.id
                && !place.isVerified!!
                && !place.isPublic!!
                && user.hasAuthority("source:modify"))   {
            return
        }else throw UnauthorizedException("Insufficient authority") //If all else fails, we throw an exception
    }

    /**
     * Update place sources
     */
    @RequestMapping("/update")
    fun updateSourceForPlace(@RequestBody sources: List<Source>, @RequestParam(name="p") id: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user has access to modify this place's sources. If not, this line will throw an exception
        checkModifyAccess(principal, id)

        sourcePlaceService.deleteSourcesById(id)

        val cats = ArrayList<SourcePlace>()
        for(t: Source in sources)
            cats.add(SourcePlace(t.sourceId!!, id))

        insertSource(cats)
    }
}