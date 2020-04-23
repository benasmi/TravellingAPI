package com.travel.travelapi.models

import java.sql.Time

data class WorkingSchedule(val dayOfWeek: Int? = null,
                           val openTime: Time? = null,
                           val closeTime: Time? = null,
                           val isClosed: Boolean? = null)