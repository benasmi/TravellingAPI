package com.travel.travelapi.models

data class PhotoPlace(val fk_photoId: Int? = null,
                 val fk_placeId: Int? = null,
                 val isFeatured: Boolean? = null,
                 val indexInList: Int? = null) {
}