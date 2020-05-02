package com.travel.travelapi.facebook.places.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.travel.travelapi.models.Place
import com.travel.travelapi.models.PlaceApi


class FacebookPlaceDeserializer: JsonDeserializer<SearchApiResponseObject>() {
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

            //TODO: add other fields
            places.add(place)
        }

        val after = node.get("paging").get("cursors")?.get("after")?.asText()
        val before = node.get("paging").get("cursors")?.get("before")?.asText()
        return SearchApiResponseObject(places.toList(), before, after)

    }

}