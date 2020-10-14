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
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
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

    enum class ScheduleState(val message: String, val type: Int) {
        OPEN("Open", 0),
        CLOSED ("Closed", 1),
        CLOSING_SOON ("Closing soon", 2),
        OPENS_SOON ("Opens soon", 3),
    }

    fun interpretScheduleState(placeId: Int): ScheduleState?{
        //TODO: support time zones
        val schedule = getRelevantSchedule(placeId) ?: return null
        val calendar = Calendar.getInstance();
        val weekdayIndex: Int
        weekdayIndex = when(calendar.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> -1
        }
        val currentPeriod = schedule.periods?.firstOrNull { it.openDay == weekdayIndex && LocalTime.parse(it.openTime, DateTimeFormatter.ofPattern("HH:mm")) <= LocalTime.now().plusHours(1) && LocalTime.now() <= LocalTime.parse(it.closeTime, DateTimeFormatter.ofPattern("HH:mm"))}
                ?: return ScheduleState.CLOSED
        if(LocalTime.parse(currentPeriod.openTime, DateTimeFormatter.ofPattern("HH:mm")) >= LocalTime.now())
            return ScheduleState.OPENS_SOON
        else if(LocalTime.now().plusHours(1) >= LocalTime.parse(currentPeriod.closeTime, DateTimeFormatter.ofPattern("HH:mm")))
            return ScheduleState.CLOSING_SOON
        else return ScheduleState.OPEN
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

    fun getRelevantSchedule(placeId: Int): WorkingSchedule?{
        var schedules = getWorkingSchedulesById(placeId)

        //If no from/to dates are specified, assuming schedule is year-round
        if(schedules.count() == 1 && (schedules[0].from == null || schedules[0].to == null))
            return schedules[0]

        val format = DateTimeFormatter.ofPattern("uuuu-M-d")
        schedules = schedules.sortedBy { schedule ->
            val date1 = LocalDate.parse("0000-" + schedule.from, format).atStartOfDay()
            var date2 = LocalDate.parse("0000-" + schedule.to, format).atStartOfDay()
            if(date2.isBefore(date1))
                date2 = date2.plusYears(1)
            Duration.between(date1, date2)
        }
        val currentMonthDay = LocalDate.parse("0000-" + LocalDate.now().month.value + "-" + LocalDate.now().dayOfMonth, format)
        schedules.forEach { schedule ->
            var to = LocalDate.parse("0000-"+schedule.to, format)
            val from = LocalDate.parse("0000-" + schedule.from, format)
            if(to.isBefore(from))
                to = to.plusYears(1)
            if(currentMonthDay.isBefore(from))
                currentMonthDay.plusYears(1)
            if(currentMonthDay.isBefore(to) && from.isBefore(currentMonthDay))
                return schedule
        }
        return null
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

//        try{
            workingScheduleService.updateWorkingScheduleForPlace(workingSchedules, placeId)
//        }catch(ex: Exception){
//            throw InvalidParamsException("The input data was incorrect")
//        }
    }

}