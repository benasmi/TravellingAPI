package com.travel.travelapi.facebook.places.api

import com.travel.travelapi.config.WebClientService
import com.travel.travelapi.exceptions.FailedApiRequestException
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.PlaceApi
import com.travel.travelapi.services.ApiPlace
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/placeApi")
class ApiPlaceController(
        @Autowired val webClientService: WebClientService,
        @Autowired val apiPlace: ApiPlace
) {



    @RequestMapping("/search")
    fun search(@RequestParam searchQuery: String?, @RequestParam center: FloatArray?, @RequestParam before: String?, @RequestParam after: String?): SearchApiResponseObject {

        if(searchQuery == null && center == null)
            throw InvalidParamsException("You must provide at least one of the following params: searchQuery, center")
        if(center != null && center.count() != 2)
            throw InvalidParamsException("Invalid param center")

        val response = webClientService.webClientBuilder()
                .build()
                .get()
                .uri{t ->  t.scheme("https")
                        .host("graph.facebook.com")
                        .path("/search")
                        .queryParam("type", "place") //averageTimeSpent address country city
                        .queryParam("center", center?.let { center[0].toString() + "," + center[1] })
                        .queryParam("q", searchQuery)
                        .queryParam("fields", "id,description,name,location,phone,website")
                        .queryParam("access_token", webClientService.facebookApiKey())
                        .queryParam("before", before)
                        .queryParam("after", after)
                        .build()}
                .retrieve()
                .bodyToMono(SearchApiResponseObject::class.java)
                .doOnError { throw FailedApiRequestException("Places could not be retrieved")}
                .block()

        response!!.data!!.forEach { placeRetrieved ->
            val place = apiPlace.selectApiPlaceById(placeRetrieved.apiPlaceId!!)
            if(place != null)
                placeRetrieved.merge(place)
        }

        return response
    }




}