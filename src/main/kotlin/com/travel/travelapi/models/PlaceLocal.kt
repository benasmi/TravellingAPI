package com.travel.travelapi.models

class PlaceLocal(val placeId: Int? = null,
                 var totalReviews: Int? = null,
                 val timeAdded: String? = null,
                 hasSchedule: Boolean? = null,
                 var isPublic: Boolean? = null,
                 var isVerified: Boolean? = null,
                 name: String? = null,
                 description: String? = null,
                 var averageTimeSpent: Int? = null,
                 latitude: Float? = null,
                 longitude: Float? = null,
                 address: String? = null,
                 country: String? = null,
                 city: String? = null,
                 phoneNumber: String? = null,
                 website: String? = null,
                 overallStarRating: Double? = null,
                 price: String? = null): Place(name, description, hasSchedule, latitude, longitude, address ,country, city, phoneNumber, website, overallStarRating, price)


