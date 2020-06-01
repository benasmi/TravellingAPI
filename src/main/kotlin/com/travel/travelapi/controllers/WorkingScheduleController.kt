package com.travel.travelapi.controllers

import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.WorkingSchedule
import com.travel.travelapi.services.WorkingScheduleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.sql.SQLException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequestMapping("/ws")
@RestController
class WorkingScheduleController(@Autowired private val workingScheduleService: WorkingScheduleService) {

    /**
     * @param placeId of a place
     * @return working schedule information related to place
     */
    @GetMapping("/{placeId}")
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
    @RequestMapping("/{placeId}/update")
    fun updateWorkingScheduleForPlace(@RequestBody workingSchedules: List<WorkingSchedule>, @PathVariable("placeId") placeId: Int){
        try{
            workingScheduleService.updateWorkingScheduleForPlace(workingSchedules, placeId)
        }catch(ex: Exception){
            throw InvalidParamsException("The input data was incorrect")
        }
    }

}