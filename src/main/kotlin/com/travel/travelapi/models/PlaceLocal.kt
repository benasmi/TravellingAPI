package com.travel.travelapi.models

import com.travel.travelapi.controllers.WorkingScheduleController


class PlaceLocal(val placeId: Int? = null,
                 var totalReviews: Int? = null,
                 val timeAdded: String? = null,
                 hasSchedule: Boolean? = null,
                 var isPublic: Boolean? = null,
                 var isVerified: Boolean? = null,
                 name: String? = null,
                 description: String? = null,
                 var averageTimeSpent: Int? = null,
                 var dateModified: String? = null,
                 val type: String? = "0",
                 latitude: Float? = null,
                 longitude: Float? = null,
                 address: String? = null,
                 country: String? = null,
                 city: String? = null,
                 phoneNumber: String? = null,
                 website: String? = null,
                 overallStarRating: Double? = null,
                 price: String? = null,
                 county: String? = null,
                 municipality: String? = null,
                 var bookInAdvance: Boolean? = null,
                 userId: Int? = null,
                 categories: List<Category>? = null,
                 grade: Int? = null): Place(name, description, hasSchedule, latitude, longitude, address
        ,country, city, county, municipality, phoneNumber, website, overallStarRating, price, userId, categories, grade)


