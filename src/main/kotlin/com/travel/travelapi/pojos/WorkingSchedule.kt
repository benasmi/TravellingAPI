package com.travel.travelapi.pojos

import java.sql.Time

data class WorkingSchedule(val dayOfWeek: Int,
                           val openTime: Time,
                           val closeTime: Time,
                           val isClosed: Boolean)