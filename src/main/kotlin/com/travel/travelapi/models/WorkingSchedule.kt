package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Time

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WorkingSchedule(val fk_placeId: Int?=null,
                           val dayOfWeek: Int? = null,
                           val openTime: Time? = null,
                           val closeTime: Time? = null)