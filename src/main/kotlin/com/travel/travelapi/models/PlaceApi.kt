package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.AddressComponent
import com.google.maps.model.AddressComponentType
import com.google.maps.model.PlaceDetails
import com.google.maps.model.PlacesSearchResult
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
class PlaceApi(var placeId: String? = null,
               var apiTypeId: Int? = null,
               hasSchedule: Boolean? = null,
               name: String? = null,
               description: String? = null,
               latitude: Float? = null,
               longitude: Float? = null,
               address: String? = null,
               country: String? = null,
               city: String? = null,
               phoneNumber: String? = null,
               website: String? = null,
               overallStarRating: Double? = null,
               county: String? = null,
               municipality: String? = null,
               val type: String? = "1"): Place(name, description, hasSchedule, latitude, longitude, address ,country, city, county, municipality, phoneNumber, website, overallStarRating){

    companion object{
        fun CreateFromSearchResponseObject(response: PlacesSearchResult): PlaceApi{
            val place = PlaceApi(response.placeId,
                    null,
                    null,
                    response.name,
                    null,
                    response.geometry.location.lat.toFloat(),
                    response.geometry.location.lng.toFloat(),
                    response.formattedAddress,
                    null,
                    null,
                    null,
                    null,
                    if(response.rating == 0f) null else response.rating.toDouble()
            )

            response.photos?.get(0)?.let { photo ->
                val url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/placeApi").path("/photo").build().toString() + "?photoreference=${photo!!.photoReference}"
                val photos = ArrayList<Photo>()
                val photoCreated = Photo(null, url, null)
                photos.add(photoCreated)
                place.photos = photos
            }

            return place
        }
        fun CreateFromDetailsResponseObject(response: PlaceDetails): PlaceApi{

            val placeApi = PlaceApi(response.placeId,
                    null,
                    response.openingHours != null,
                    response.name,
                    null,
                    response.geometry.location.lat.toFloat(),
                    response.geometry.location.lng.toFloat(),
                    response.formattedAddress,
                    getAddressComponent(response.addressComponents, AddressComponentType.COUNTRY),
                    getAddressComponent(response.addressComponents, AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_2),
                    response.formattedPhoneNumber,
                    response.website?.toString(),
                    if(response.rating == 0f) null else response.rating.toDouble()
            )

            val photos = ArrayList<Photo>()
            response.photos?.forEach{ photo ->
                val url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/placeApi").path("/photo").build().toString() + "?photoreference=${photo!!.photoReference}"
                val photoCreated = Photo(null, url, null)
                photos.add(photoCreated)
            }
            placeApi.photos = photos

            val categories = ArrayList<Category>()

            response.types?.forEach {category ->
                categories.add(Category(category.toCanonicalLiteral()))
            }
            placeApi.categories = categories

            if(response.openingHours !== null && response.openingHours.periods != null){
                val schedule = ArrayList<WorkingSchedule>()
                for(i in 0..6 ){
                    schedule.add(WorkingSchedule(null, i, null, null, true))
                }
                response.openingHours.periods?.forEach  lit@{ period ->
                    var dayOfWeek: Int? = null
                    when (period.open.day.ordinal) {
                        0 -> dayOfWeek = 6
                        1 -> dayOfWeek = 0
                        2 -> dayOfWeek = 1
                        3 -> dayOfWeek = 2
                        4 -> dayOfWeek = 3
                        5 -> dayOfWeek = 4
                        6 -> dayOfWeek = 5
                    }
                    if(dayOfWeek == null)
                        return@lit
                    schedule[dayOfWeek] = WorkingSchedule(null, dayOfWeek, period.open?.time?.toString(), period.close?.time?.toString(), false)
                }

                placeApi.schedule = schedule
            }

            return placeApi
        }

        private fun getAddressComponent(addressComponents: Array<AddressComponent>, key: AddressComponentType): String? {
            addressComponents.forEach { component ->
                if(component.types.contains(key)){
                    return component.longName
                }
            }
            return null
        }
    }

    fun merge(place: PlaceApi){
        description = place.description ?: description
    }
}
