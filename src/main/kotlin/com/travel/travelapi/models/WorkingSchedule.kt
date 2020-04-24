package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import com.travel.travelapi.TestView
import java.sql.Time

data class WorkingSchedule(@JsonView(TestView.Public::class)val fk_placeId: Int?=null,
                           val dayOfWeek: Int? = null,
                           val openTime: Time? = null,
                           val closeTime: Time? = null,
                           val isClosed: Boolean? = null)