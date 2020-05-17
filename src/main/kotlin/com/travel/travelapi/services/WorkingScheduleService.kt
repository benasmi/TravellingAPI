package com.travel.travelapi.services

import com.travel.travelapi.models.WorkingSchedule
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
interface WorkingScheduleService {
    @Select("SELECT dayOfWeek,openTime,closeTime, isClosed FROM WORKING_SCHEDULE WHERE fk_placeId=#{id}")
    fun selectWorkingScheduleById(@Param("id") id: Int): List<WorkingSchedule>

    @Insert("INSERT INTO WORKING_SCHEDULE (fk_placeId, dayOfWeek,openTime,closeTime,isClosed) VALUES (#{ws.fk_placeId}, #{ws.dayOfWeek},#{ws.openTime}, #{ws.closeTime}, #{ws.isClosed})")
    fun insertWorkingScheduleForPlace(@Param("ws") ws: WorkingSchedule)
    
    @Delete("DELETE FROM WORKING_SCHEDULE WHERE fk_placeId=#{id}")
    fun deleteSchedule(@Param("id") id: Int)
}