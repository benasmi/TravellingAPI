package com.travel.travelapi.services

import com.travel.travelapi.models.WorkingSchedule
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface WorkingScheduleService {
    fun selectWorkingScheduleById(@Param("id") id: Int): List<WorkingSchedule>

    fun insertWorkingScheduleForPlace(@Param("ws") ws: WorkingSchedule)
    
    fun deleteSchedule(@Param("id") id: Int)
}