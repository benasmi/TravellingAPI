package com.travel.travelapi.services

import com.travel.travelapi.models.Period
import com.travel.travelapi.models.WorkingSchedule
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface WorkingScheduleService {

    fun selectWorkingSchedulesById(@Param("targetPlaceId") id: Int): List<WorkingSchedule>

    fun selectPeriodsById(@Param("id") id: Int): List<Period>

    fun updateWorkingScheduleForPlace(@Param("wsList") workingSchedules: List<WorkingSchedule>, @Param("placeId") placeId: Int)

    fun deleteSchedule(@Param("id") id: Int)
}