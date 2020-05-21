package com.travel.travelapi.models

class PlaceLocal(val placeId: Int? = null,
                 hasSchedule: Boolean? = null,
                 isPublic: Boolean? = null,
                 isVerified: Boolean? = null,
                 name: String? = null,
                 description: String? = null,
                 averageTimeSpent: String? = null,
                 latitude: Float? = null,
                 longitude: Float? = null,
                 address: String? = null,
                 country: String? = null,
                 city: String? = null,
                 phoneNumber: String? = null,
                 website: String? = null,
                 overallStarRating: Double? = null): Place(name, description, hasSchedule, isPublic, isVerified, averageTimeSpent, latitude, longitude, address ,country, city, phoneNumber, website, overallStarRating)