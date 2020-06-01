package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Time

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Period(var id: Int,
                           var scheduleId: Int? = null,
                           var openDay: Int? = null,
                           var openTime: String? = null,
                            var closeDay: Int? = null,
                            var closeTime: String? = null)