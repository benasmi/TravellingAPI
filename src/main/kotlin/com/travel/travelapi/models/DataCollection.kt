package com.travel.travelapi.models

import com.travel.travelapi.controllers.ExplorePageController
import java.util.*

data class SearchedPlace(val fk_placeId: Int? = null,
                        val fk_userId: Int? = null,
                        val device: String? = null,
                        val insertionDate: Date? = null)

data class SearchedTour(val fk_tourId: Int? = null,
                         val fk_userId: Int? = null,
                         val device: String? = null,
                         val insertionDate: Date? = null)

data class SearchedLocation(val exploreLocation: ExplorePageController.ExploreLocation? = null,
                        val fk_userId: Int? = null,
                        val device: String? = null,
                        val insertionDate: Date? = null)