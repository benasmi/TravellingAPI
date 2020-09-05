package com.travel.travelapi.controllers

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.travel.travelapi.exceptions.InvalidUserDataException
import com.travel.travelapi.models.Location
import com.travel.travelapi.models.Tour
import com.travel.travelapi.models.TourDayInfo
import com.travel.travelapi.models.TransportFrom
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.TransportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
class TransportController(@Autowired private val transportService: TransportService,
                          @Autowired private val placeService: PlaceService) {

    /**
     * Get transport associated with local place in a tour
     * @param id of place tour day
     * @return transport
     */
    fun getLocalPlaceTransportFrom(id: Int): TransportFrom{
        return transportService.getLocalPlaceTransportFrom(id)
    }

    /**
     * Attaches travelling info(distance, duration) between tour places
     */
    fun calculateTourDaysTravellingInfo(tour: Tour){
        val days = tour.days
        days?.forEach {
            it.data = getTransportInfoBetweenTwoPoints(it.data!!)
        }
    }

    /**
     * Method calculates distance and duration between each distance based on driving mode
     *
     * @see it is written in a way to minimize Distance Matrix API calls. If
     * count of API calls is not important, it can be re-written in a way to not look for
     * consequent driving modes between places
     *
     */
    private fun getTransportInfoBetweenTwoPoints(tourDayPlaces: ArrayList<TourDayInfo>): ArrayList<TourDayInfo>{
        //Attach latitude and longitude to place
        tourDayPlaces.forEach {
            val location = placeService.getPlaceLocation(it.place?.placeId!!)
            it.place!!.latitude = location.latitude
            it.place!!.longitude = location.longitude
        }

        //Temporal array for storing consequent places with same driving mode
        var placesInfo = ArrayList<TourDayInfo>()
        //Store final results with calculated durations and distances
        val transportInfo = ArrayList<TourDayInfo>()
        var previousTransport = selectTrueTransport(tourDayPlaces[0].transport?.fk_transportId!!);

        //Check all places and find consequent driving modes between places
        for(i in 0 until tourDayPlaces.size){
          val currentTransport = selectTrueTransport(tourDayPlaces[i].transport?.fk_transportId)
          placesInfo.add(TourDayInfo(tourDayPlaces[i].place, tourDayPlaces[i].transport))
            if(currentTransport != previousTransport){
                calculateOriginAndDestination(placesInfo)

                //Found different driving modes, but list still has unchecked elements
                if(transportInfo.size>0){
                    transportInfo.removeAt(transportInfo.lastIndex)
                }

                transportInfo.addAll(placesInfo)
                placesInfo = ArrayList()
                placesInfo.add(TourDayInfo(tourDayPlaces[i].place, tourDayPlaces[i].transport))

            }

            previousTransport = currentTransport
        }
        return transportInfo
    }


    /**
     * Method does GET request to Distance Matrix API with origins, destinations, driving mode
     * and returns matrix of distances and durations
     *
     * @sample given A->B->C
     * and forming origins and destinations as
     *
     * ?origins=A|B&destinations=B|C
     *
     * it will return matrix of
     *
     * A->B A->C
     * B->B B->C
     *
     * Further parsing required to get A->B and B->C
     *
     * @see 'https://developers.google.com/maps/documentation/distance-matrix/overview'
     */
    private fun calculateOriginAndDestination(places: ArrayList<TourDayInfo>){
        val query = buildOriginAndDestinationQuery(places)

        val restTemplate = RestTemplate()
        val res: String = restTemplate.getForObject<String>("https://maps.googleapis.com/maps/api/distancematrix/json$query&key=AIzaSyBzqv8HIoee9ghRHm5k8_8FfAtYfqfTOyw", String::class.java)!!
        val json = Gson().fromJson(res, JsonObject::class.java)!!

        parseDistanceMatrix(json, places)
    }

    /**
     * Method parses required info from Distance Matrix API that is in format of a matrix
     * During parsing it sets actual distances and durations for given places
     *
     * All required information lies in diagonal of a matrix.
     * Response matrix can be compared with unitary matrix
     *
     * 1 0 0
     * 0 1 0
     * 0 0 1 etc.
     *
     * @see 'https://developers.google.com/maps/documentation/distance-matrix/overview'
     */
    private fun parseDistanceMatrix(jsonObject: JsonObject, places: ArrayList<TourDayInfo>){
        val rows = jsonObject.get("rows").asJsonArray

        for((column, i) in (0 until rows.size()).withIndex()){
            val elements = rows[i].asJsonObject.get("elements").asJsonArray
            val status = elements[column].asJsonObject.get("status").asString
            var distance = 0;
            var duration = 0;
                if(status == "OK"){
                    distance = elements[column].asJsonObject.get("distance").asJsonObject.get("value").asInt
                    duration = elements[column].asJsonObject.get("duration").asJsonObject.get("value").asInt


                }
            places[column].transport?.distance = distance
            places[column].transport?.duration = duration
        }
    }


    /**
     * Build query of origin,destinations and travelling mode for Google Distance Matrix API call
     * @param places that have consiquent travelling mode
     * @return query string
     *
     * @see 'https://developers.google.com/maps/documentation/distance-matrix/overview'
     */
    private fun buildOriginAndDestinationQuery(places: ArrayList<TourDayInfo>): String{
        val origins = ArrayList<String>()
        val destinations = ArrayList<String>()
        val transport: String = transportSelector(places[0].transport?.fk_transportId!!)

        for(i in 0 until places.size){
            when (i) {
                0 -> {
                    origins.add(""+places[i].place?.latitude + "," +places[i].place?.longitude)
                }
                places.size-1 -> {
                    destinations.add(""+places[i].place?.latitude + "," +places[i].place?.longitude)
                }
                else -> {
                    origins.add(""+places[i].place?.latitude + "," +places[i].place?.longitude)
                    destinations.add(""+places[i].place?.latitude + "," +places[i].place?.longitude)
                }
            }
        }

        return "?origins="+origins.joinToString("|")+"&destinations="+destinations.joinToString("|")+"&mode="+transport
    }

    /**
     * Driving mode selector.
     * Currently there is no bicycling mode
     */
    private fun transportSelector(id: Int): String{
        when(id){
            4,2->return "driving"
            1,3,5,6,8->return "transit"
            7->return "walking"
        }
        return ""
    }


    /**
     * Google maps does not have bicycling, walking or driving coverage around the globe.
     * So for Lithuania and Latvia given transport type driving and bicycling(2,4)
     * backend will see it as driving. Same goes for transit modes(1,3,5,6,8).
     */
    private fun selectTrueTransport(current: Int?): Int{
         current?: return -1
         when (current) {
            2, 4 -> return 4
            1, 3, 5, 6, 8 -> return 6
            7 -> return 7
        }
        return -1
    }


}



