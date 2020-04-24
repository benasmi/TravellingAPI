package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Time

data class WorkingSchedule(@JsonIgnore val fk_placeId: Int? = null,
                           val dayOfWeek: Int? = null,
                           val openTime: Time? = null,
                           val closeTime: Time? = null,
                           val isClosed: Boolean? = null)