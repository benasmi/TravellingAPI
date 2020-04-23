package com.travel.travelapi.services

import com.travel.travelapi.models.Parking
import com.travel.travelapi.models.WorkingSchedule
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Repository

@Repository
interface WorkingScheduleService {

    @Select("SELECT * FROM WORKING_SCHEDULE WHERE fk_placeId=#{id}")
    fun selectWorkingScheduleById(@Param("id") id: Int): List<WorkingSchedule>

}