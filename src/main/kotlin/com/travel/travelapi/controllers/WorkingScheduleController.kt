package com.travel.travelapi.controllers

import com.travel.travelapi.models.WorkingSchedule
import com.travel.travelapi.services.WorkingScheduleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkingScheduleController(@Autowired private val workingScheduleService: WorkingScheduleService) {


    /**
     * @param placeId of a place
     * @return working schedule information related to place
     */
    fun getWorkingScheduleById(placeId: Int): List<WorkingSchedule>{
        return workingScheduleService.selectWorkingScheduleById(placeId)
    }
}