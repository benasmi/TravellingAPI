package com.travel.travelapi.services

import com.travel.travelapi.models.WorkingSchedule
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Repository

@Repository
interface WorkingScheduleService {

    @Select("SELECT * FROM WORKING_SCHEDULE WHERE fk_placeId=#{id}")
    fun selectWorkingScheduleById(@Param("id") id: Int): List<WorkingSchedule>

    @Insert("INSERT INTO WORKING_SCHEDULE (fk_placeId, dayOfWeek,openTime,closeTime,isClosed) VALUES (#{ws.fk_placeId}, #{ws.dayOfWeek},#{ws.openTime}, #{ws.closeTime}, #{ws.isClosed})")
    fun insertWorkingScheduleForPlace(@Param("ws") ws: WorkingSchedule)

    @Update("UPDATE WORKING_SCHEDULE SET openTime=#{ws.openTime}, closeTime=#{ws.closeTime}, isClosed=#{ws.isClosed} WHERE fk_placeId=#{ws.fk_placeId} AND dayOfWeek=#{ws.dayOfWeek}")
    fun updateWorkingSchedule(@Param("ws") ws: WorkingSchedule)

}