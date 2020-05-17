package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Time

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WorkingSchedule(var fk_placeId: Int? = null,
                           var dayOfWeek: Int? = null,
                           var openTime: String? = null,
                           var closeTime: String? = null,
                           var isClosed: Boolean? = null)