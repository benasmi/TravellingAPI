package com.travel.travelapi.facebook.places.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.travel.travelapi.models.*
import java.sql.Time
import java.time.LocalDateTime


class FacebookPlaceDeserializer: JsonDeserializer<SearchApiResponseObject>() {

    val apiDaysOfWeek = arrayOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")

    override fun deserialize(jp: JsonParser?, p1: DeserializationContext?): SearchApiResponseObject {

        val places = ArrayList<PlaceApi>()

        val oc: ObjectCodec = jp!!.codec
        val node: JsonNode = oc.readTree(jp)

        val dataNode = node.get("data")

        dataNode.asIterable().forEach { item ->
            val id = item.get("id").asText()
            val place = PlaceApi(id)

            place.description = item.get("description")?.asText()
            place.name = item.get("name").asText()
            place.phoneNumber = item.get("phone")?.asText()
            place.website = item.get("website")?.asText()

            val locationNode = item.get("location")
            place.latitude = locationNode.get("latitude")?.asDouble()?.toFloat()
            place.longitude = locationNode.get("longitude")?.asDouble()?.toFloat()
            place.city = locationNode.get("city")?.asText()
            place.address = locationNode.get("street")?.asText()

            val categories: ArrayList<Category> = ArrayList()

            item.get("category_list")?.asIterable()?.forEach { category ->
                val categoryName = category.get("name").asText()
                categories.add(Category(categoryName))
            }
            place.categories = categories

            val photos = ArrayList<Photo>()
            if(item.has("cover")) {
                photos.add(Photo(null, item.get("cover").get("source").asText(), null))
                place.photos = photos
            }

            if(item.has("overall_star_rating")){
                place.overallStarRating = item.get("overall_star_rating").asDouble()
            }

            if(item.has("hours")){
                val schedule = ArrayList<WorkingSchedule>()
                val workingDays = item.get("hours").asIterable()

                for (i in 0..6){
                    val openTimeItem = workingDays.find{value -> value.get("key").asText() == (apiDaysOfWeek[i] + "_1_" + "open") }
                    val closeTimeItem = workingDays.find{value -> value.get("key").asText() == (apiDaysOfWeek[i] + "_1_" + "close") }

                    if(openTimeItem != null && closeTimeItem != null){
                        val dayOfWeek = WorkingSchedule(null, i, openTimeItem.get("value").asText()!!, closeTimeItem.get("value").asText()!!, false)
                        schedule.add(dayOfWeek)
                    }
                }
                place.schedule = schedule
            }

            places.add(place)
        }

        val after = node.get("paging").get("cursors")?.get("after")?.asText()
        val before = node.get("paging").get("cursors")?.get("before")?.asText()

        return SearchApiResponseObject(places.toList(), before, after)

    }

}