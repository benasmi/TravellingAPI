package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WorkingSchedule(var id: Int? = null,
                           var placeId: Int? = null,
                           var from: String? = null,
                           var to: String? = null,
                           var isDefault: Boolean? = null) {

    var periods: List<Period>? = null
}