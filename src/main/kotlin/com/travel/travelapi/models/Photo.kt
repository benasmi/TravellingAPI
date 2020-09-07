package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Photo(val photoId: Int? = null,
                 val url: String? = null,
                 val date: Date? = null,
                 val userId: Int? = null) {

}