package com.travel.travelapi.controllers

import com.travel.travelapi.models.WorkingSchedule
import com.travel.travelapi.services.WorkingScheduleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RequestMapping("/ws")
@RestController
class WorkingScheduleController(@Autowired private val workingScheduleService: WorkingScheduleService) {


    /**
     * @param placeId of a place
     * @return working schedule information related to place
     */
    @ResponseBody fun getWorkingScheduleById(placeId: Int): List<WorkingSchedule>{
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

    /**
     * Update working schedule for a place
     */
    @RequestMapping("/update")
    fun updateWorkingScheduleForPlace(@RequestBody workingSchedules: List<WorkingSchedule>, @RequestParam(name = "p") id: Int){
        workingScheduleService.deleteSchedule(id)
        for(ws: WorkingSchedule in workingSchedules){
            ws.fk_placeId = id
            workingScheduleService.insertWorkingScheduleForPlace(ws)
        }
    }

}