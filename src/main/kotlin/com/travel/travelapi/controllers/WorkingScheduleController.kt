package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.WorkingSchedule
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.WorkingScheduleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.io.Console
import java.sql.SQLException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequestMapping("/ws")
@RestController
class WorkingScheduleController(@Autowired private val workingScheduleService: WorkingScheduleService,
                                @Autowired private val placeService: PlaceService) {

    /**
     * Checks if user has modify access for the given place's working schedule
     */
    @Throws(UnauthorizedException::class)
    fun checkModifyAccess(user: TravelUserDetails, placeId: Int){
        //Getting the place user wishes modify schedule for
        val place = placeService.selectById(placeId)

        //Checking if user has unrestricted access to modify working schedule for any place
        if(user.hasAuthority("workingschedule:modify_unrestricted")){
            return
            //If user is not authorized to modify working schedule for any place, we check if the user has created this place, has not published it and admin has not verified it, and has relevant permission
        }else if(user.id!! == (place.userId?.toLong() ?: -1)
                && !place.isVerified!!
                && !place.isPublic!!
                && user.hasAuthority("workingschedule:modify")){
            return
        }else throw UnauthorizedException("User does not have permission to modify schedule for this place") //Denying access to user
    }

    /**
     * @param placeId of a place
     * @return working schedule information related to place
     */
    @GetMapping("/{placeId}")
    @PreAuthorize("hasAuthority('workingschedule:read')")
    fun getWorkingSchedulesById(@PathVariable("placeId") placeId: Int?): List<WorkingSchedule>{
        val schedules = workingScheduleService.selectWorkingSchedulesById(placeId!!)
        schedules.forEach {ws ->
            ws.periods = workingScheduleService.selectPeriodsById(ws.id!!)
        }
        return schedules
    }

    /**
     * Insert working schedule for a place
     */
    @RequestMapping("/update")
    fun updateWorkingScheduleForPlace(@RequestBody workingSchedules: List<WorkingSchedule>, @RequestParam("id") placeId: Int){

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Checking if user is authorized to modify this place's working schedule. This line will throw an exception if that is not the case
        checkModifyAccess(principal, placeId)

        try{
            workingScheduleService.updateWorkingScheduleForPlace(workingSchedules, placeId)
        }catch(ex: Exception){
            throw InvalidParamsException("The input data was incorrect")
        }
    }

}