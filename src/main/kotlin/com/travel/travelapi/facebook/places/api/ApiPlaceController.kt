package com.travel.travelapi.facebook.places.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.maps.PlacesApi
import com.google.maps.errors.InvalidRequestException
import com.travel.travelapi.config.GeoApiContextInstance
import com.travel.travelapi.config.WebClientService
import com.travel.travelapi.exceptions.FailedApiRequestException
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.PlaceApi
import com.travel.travelapi.services.ApiPlace
import org.apache.commons.text.similarity.LevenshteinDistance
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.lang.Integer.max
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


@RestController
@RequestMapping("/placeApi")
class ApiPlaceController(
        @Autowired val webClientService: WebClientService,
        @Autowired val apiPlace: ApiPlace,
        @Autowired val geoApiContext: GeoApiContextInstance
) {

    @GetMapping("/search")
    fun search(@RequestParam keyword: String?, @RequestParam center: FloatArray?): List<PlaceApi>{
        if(keyword == null && center == null)
            throw InvalidParamsException("You must provide at least one of the following params: keyword, center")
        if(center != null && center.count() != 2)
            throw InvalidParamsException("Invalid param center")

        val places = PlacesApi.textSearchQuery(geoApiContext.getGeoApiContext(), keyword).await()
        val placesDeserialized = ArrayList<PlaceApi>()
        places.results.forEach {
            val place = PlaceApi.CreateFromSearchResponseObject(it)
//            if (it.types.any{item -> item.equals("tourist_attraction") || item.equals("park")})
                place.description = getPlaceDescriptionWiki(place)
            placesDeserialized.add(place)
        }

        return placesDeserialized
    }

    @GetMapping("/photo")
    fun photo(@RequestParam("photoreference") photoRef: String,
              @RequestParam(required=false, name="maxWidth", defaultValue="500") maxWidth: Int?): ResponseEntity<ByteArray> {

        try {val photo = PlacesApi.photo(geoApiContext.getGeoApiContext(), photoRef).maxWidth(maxWidth!!).await()
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(photo.contentType))
                    .body(photo.imageData)
        }
        catch (ex: IOException){throw InvalidParamsException("The photo reference provided is invalid")}
    }

    private fun getPlaceDescriptionWiki(place: PlaceApi): String{
        val response = webClientService.webClientBuilder()
                .build()
                .get()
                .uri{t ->  t.scheme("https")
                        .host("en.wikipedia.org")
                        .path("/w")
                        .path("/api.php")
                        .queryParam("format", "json")
                        .queryParam("action", "query")
                        .queryParam("redirects", "1")
                        .queryParam("generator", "geosearch")
                        .queryParam("prop", "extracts|coordinates|pageimages")
                        .queryParam("ggslimit", "20")
                        .queryParam("ggsradius", "1000")
                        .queryParam("ggscoord", place.latitude.toString() + "|" + place.longitude.toString())
                        .queryParam("explaintext", "1")
                        .queryParam("exintro", "1")
                        .queryParam("coprop", "type|dim|globe")
                        .queryParam("colimit", "20")
                        .queryParam("piprop", "thumbnail")
                        .queryParam("pithumbsize", "400")
                        .queryParam("pilimit", "20")
                        .build()}
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

        val gson = Gson().fromJson(response, JsonObject::class.java)
        val pages = gson?.get("query")?.asJsonObject?.get("pages")?.asJsonObject
        var description: String? = null

        if(pages == null)
            return ""

        val bestResult = pages.keySet()?.filter { item ->
            val placeWiki1 = pages.get(item).asJsonObject
            val title = placeWiki1.get("title").asString.toLowerCase()
            val error = LevenshteinDistance.getDefaultInstance().apply(title, place.name).toDouble() / max(place.name!!.length, title.length).toDouble()
            error <= 0.5
        }?.
        minWith( Comparator<String> {item1, item2 ->
            val placeWiki1 = pages.get(item1).asJsonObject
            val placeWiki2 = pages.get(item2).asJsonObject

            val location1 = placeWiki1.get("coordinates").asJsonArray.get(0).asJsonObject
            val location2 = placeWiki2.get("coordinates").asJsonArray.get(0).asJsonObject

            val lat1 = location1.get("lat").asDouble
            val lng1 = location1.get("lon").asDouble

            val lat2 = location2.get("lat").asDouble
            val lng2 = location2.get("lon").asDouble

            val d1 = distance(lat1, lng1, place.latitude!!.toDouble(), place.longitude!!.toDouble())
            val d2 = distance(lat2, lng2, place.latitude!!.toDouble(), place.longitude!!.toDouble())

            val comparison1 = d1
            val comparison2 = d2
            comparison1.compareTo(comparison2)
        })
        description = pages.get(bestResult)?.asJsonObject?.get("extract")?.asString

        return Jsoup.parse(description ?: "").text()
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    @GetMapping("/details")
    fun search(@RequestParam id: String): PlaceApi?{
        try{
            val fetchedPlace = PlacesApi.placeDetails(geoApiContext.getGeoApiContext(), id).await()
            val place = PlaceApi.CreateFromDetailsResponseObject(fetchedPlace)
            place.description = getPlaceDescriptionWiki(place)
            return place
        }catch (ex: InvalidRequestException){
            throw InvalidParamsException("Place could not be retrieved. Please check if the place ID you provided is valid")
        }catch(ex: Exception){
            throw FailedApiRequestException("Could not retrieve place. Please try again later")
        }
    }




}