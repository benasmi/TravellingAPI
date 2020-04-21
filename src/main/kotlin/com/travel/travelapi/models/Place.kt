package com.travel.travelapi.models

import com.travel.travelapi.pojos.Category
import com.travel.travelapi.pojos.Parking
import com.travel.travelapi.pojos.Review
import com.travel.travelapi.pojos.WorkingSchedule
import java.time.Duration

data class Place (val name: String,
                  val description: String,
                  val placeId: Int,
                  val averageTimeSpent: Duration,
                  val latitude: Float,
                  val longitude: Float,
                  val address: String,
                  val country: String,
                  val city: String,
                  val phoneNumber: String,
                  val website: String,
                  val categories: List<Category>,
                  val parking: List<Parking>,
                  val reviews: List<Review>,
                  val schedule: List<WorkingSchedule>)