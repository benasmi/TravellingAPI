package com.travel.travelapi.controllers

import com.travel.travelapi.models.TagPlace
import com.travel.travelapi.models.WorkingSchedule
import com.travel.travelapi.services.WorkingScheduleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/ws")
@RestController
class WorkingScheduleController(@Autowired private val workingScheduleService: WorkingScheduleService) {


    /**
     * @param placeId of a place
     * @return working schedule information related to place
     */
    fun getWorkingScheduleById(placeId: Int): List<WorkingSchedule>{
        return workingScheduleService.selectWorkingScheduleById(placeId)
    }

    /**
     * Insert working schedule for a place
     */
    @RequestMapping("/insert")
    fun insertWorkingScheduleForPlace(@RequestBody workingSchedules: List<WorkingSchedule>){
        for(ws: WorkingSchedule in workingSchedules){
            workingScheduleService.insertWorkingScheduleForPlace(ws)
        }
    }
}