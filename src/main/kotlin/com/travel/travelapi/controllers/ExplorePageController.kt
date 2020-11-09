package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.*
import com.travel.travelapi.models.search.CategoryAbstractionInfo
import com.travel.travelapi.models.search.SearchPreview
import com.travel.travelapi.models.search.SearchRequest
import com.travel.travelapi.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.HashMap
import java.util.stream.Collectors


@RestController
@RequestMapping("/explore")
class ExplorePageController(
        @Autowired private val explorePageService: ExplorePageService,
        @Autowired private val recommendationController: RecommendationController,
        @Autowired private val photoPlaceService: PhotoPlaceService,
        @Autowired private val placeController: PlaceController,
        @Autowired private val placeService: PlaceService,
        @Autowired private val tourController: TourController,
        @Autowired private val categoryService: CategoryService,
        @Autowired private val tourService: TourService,
        @Autowired private val recommendationService: RecommendationService,
        @Autowired private val categoryPlaceService: CategoryPlaceService,
        @Autowired private val dataCollectionController: DataCollectionController,
        @Autowired private val workingScheduleController: WorkingScheduleController
) {

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('explore:update')")
    fun update(@RequestBody recommendations: List<Int>) {
        if (recommendations.distinct().count() == recommendations.count()) {
            explorePageService.update(recommendations)
        } else {
            throw InvalidParamsException("The explore page cannot have any duplicate recommendations")
        }
    }

    class PageInfoCollectionObject(data: List<CollectionObject>) : PageInfo<CollectionObject>(data)

    @GetMapping("/locationCategory")
    @PreAuthorize("hasAuthority('explore:locationByCategory')")
    fun getLocationByCategory(
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int,
            @RequestParam(required = true) categoryId: Int,
            @RequestParam(required = false) location: String?,
            @RequestParam(required = false) locationType: String?,
            @RequestParam(required = false) latitude: Float?,
            @RequestParam(required = false) longitude: Float?
    ): PageInfoCollectionObject {

        PageHelper.startPage<CollectionObject>(p, s)

        val objects: List<CollectionObject>;

        if (location != null && locationType != null) {
            //Creating ExploreLocation object so we can verify that locationType is valid (to prevent SQL injection)
            val exploreLocationObject = ExploreLocation(location, locationType)

            if (!exploreLocationObject.valid())
                throw InvalidParamsException("Location type is invalid")

            objects = explorePageService.selectObjectsByLocationAndCategory(exploreLocationObject.location, exploreLocationObject.type, categoryId)

        } else if (latitude != null && longitude != null) {
            objects = explorePageService.selectObjectsByCoordsAndCategory(latitude, longitude, categoryId, 50f)
        } else {
            throw InvalidParamsException("Either location and locationType or latitude and longitude must be specified.")
        }

        objects.forEach { item ->
            if (item is CollectionObjectPlace) {
                item.setData(placeController.getPlaceById(id = item.id!!))
            } else if (item is CollectionObjectTour) {
                item.setData(tourController.tourOverviewById(item.id!!))
            }
        }

        return PageInfoCollectionObject(objects)
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('explore:read')")
    fun getExplorePage(
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int
    ): PageInfo<Recommendation> {
        PageHelper.startPage<Recommendation>(p, s)

        val recommendations = explorePageService.selectAll()

        for (recommendation in recommendations)
            recommendationController.extendRecommendation(recommendation)

        return PageInfo(recommendations)
    }


    @GetMapping("/search")
    @PreAuthorize("hasAuthority('explore:search')")
    fun search(@RequestParam keyword: String): ClientSearchResult {
        if (keyword == "")
            return ClientSearchResult(MiscellaneousCollection(objects = arrayListOf()), MiscellaneousCollection(objects = arrayListOf()), arrayListOf())

        val places = placeService.searchClient(keyword)
        val tours = getToursAssociatedWithPlaces(places)
        for (place in places) {
            val photos = photoPlaceService.selectPhotosById(place.placeId!!)
            place.photos = if (photos.count() > 0) arrayListOf(photos[0]) else arrayListOf()
        }

        return ClientSearchResult(
                MiscellaneousCollection(objects = places.map { place -> CollectionObjectPlace.createFromPlaceInstance(place) }.toMutableList()),
                MiscellaneousCollection(objects = tours.map { tour -> CollectionObjectTour.createFromTourInstance(tour) }.toMutableList()),
                explorePageService.matchSearch(keyword))
    }


    fun getToursAssociatedWithPlacesIdx(ids: List<Int>): ArrayList<Tour> {
        val tourIdx = tourService.findToursRelatedWithPlaceIds(ids)
        val tours = ArrayList<Tour>()
        tourIdx.parallelStream().forEach {
            tours.add(tourController.tourOverviewById(it))
        }
        return tours
    }

    //todo: remove
    fun getToursAssociatedWithPlaces(places: List<PlaceLocal>): List<Tour> {
        val tours = arrayListOf<Tour>()
        for (place in places) {
            val tourIds = explorePageService.selectToursForPlace(place.placeId!!)
            for (tour in tourIds)
                tours.add(tourController.tourOverviewById(tour.tourId!!))
        }
        return tours.distinctBy { tour -> tour.tourId }
    }

    data class ExploreLocation(val location: String = "", val type: String = "") {
        fun valid(): Boolean {
            return type == "city" || type == "country" || type == "municipality" || type == "county"
        }
    }

    fun extendPlaces(places: Collection<PlaceLocal>) {
        places.map { place ->
            val photos = photoPlaceService.selectPhotosById(place.placeId!!)
            place.photos = if (photos.count() > 0) arrayListOf(photos[0]) else arrayListOf()
            place.categories = categoryPlaceService.selectByPlaceId(place.placeId)
        }
    }

    fun extendPlace(place: CollectionObject) {
        val photos = photoPlaceService.selectPhotosById(place.id!!)
        place.photos = if (photos.count() > 0) arrayListOf(photos[0]) else arrayListOf()
    }


    @GetMapping("/locationRadius")
    @PreAuthorize("hasAuthority('explore:location')")
    fun getNearbyRadius(@RequestParam("lat") latitude: Float, @RequestParam("lng") longitude: Float): List<CollectionObjectPlace> {
        val places = placeService.selectPlacesInRadius(
                latitude = latitude.toDouble(),
                longitude = longitude.toDouble(),
                range = 50.0,
                limit = 10)

        extendPlaces(places)
        return places.map { CollectionObjectPlace.createFromPlaceInstance(it) }
    }

    @GetMapping("/locationBounds")
    @PreAuthorize("hasAuthority('explore:location')")
    fun getNearbyBounds(
            @RequestParam("minLat") minLat: Float,
            @RequestParam("maxLat") maxLat: Float,
            @RequestParam("minLng") minLng: Float,
            @RequestParam("maxLng") maxLng: Float): List<CollectionObjectPlace> {
        val places = placeService.selectPlacesInBounds(
                minLat = minLat,
                maxLat = maxLat,
                minLng = minLng,
                maxLng = maxLng,
                limit = 10)

        extendPlaces(places)
        return places.map { CollectionObjectPlace.createFromPlaceInstance(it) }
    }


    @PostMapping("/searchPreview")
    fun searchPreview(@RequestBody request: SearchRequest): SearchPreview {
        if (!request.exploreLocation.valid())
            throw InvalidParamsException("Explore location type is not valid")
        val averageCoords = explorePageService.averageCoordinatesForLocation(request.exploreLocation.location, request.exploreLocation.type)
        val categoryInfo = explorePageService.previewCategories(request, averageCoords)
        return SearchPreview(explorePageService.searchPlaces(request, averageCoords).count(), categoryInfo)
    }

    @PostMapping("/location")
    @PreAuthorize("hasAuthority('explore:location')")
    fun getByLocation1(@RequestParam(required = false, defaultValue = "") latLng: String,
                       @RequestParam(required = false, defaultValue = "0") p: Int,
                       @RequestParam(required = false, defaultValue = "10") s: Int,
                       @RequestBody searchRequest: SearchRequest?
    ): LogicalPage {

        if (latLng.isEmpty() && !searchRequest!!.exploreLocation.valid())
            throw InvalidParamsException("Type specified is invalid")
        if (latLng.isEmpty() && searchRequest!!.exploreLocation.location == "")
            throw InvalidParamsException("No location specified")

        //Get unique categories
        val categoriesAll = categoryService.allUniqueCategories()
        val hashMap: HashMap<String, ObjectCollection> = HashMap()

        //Create hashTable
        categoriesAll.forEach {
            hashMap[it.name!!] = SuggestionByCategory(it, objects = ArrayList())
        }

        val placesFound = if (latLng.isEmpty()) {
            val averageCoords = explorePageService.averageCoordinatesForLocation(searchRequest!!.exploreLocation.location, searchRequest.exploreLocation.type)
            explorePageService.searchPlaces(searchRequest, averageCoords)
        } else {
            placeService.selectAllAdmin(location = latLng.split(','), range = 50.0)
        }

        placesFound.parallelStream().forEach {
            it.categories = categoryPlaceService.selectByPlaceId(it.placeId!!)
        }
        val ids = placesFound.parallelStream().map { it.placeId!! }.collect(Collectors.toList())

        //Match tours
        val tours = if (ids.isNotEmpty()) getToursAssociatedWithPlacesIdx(ids = ids) else ArrayList()

        //Add tours to hashTable
        tours.parallelStream().map { it ->
            it.categories = tourService.getCategoriesForTour(it.tourId!!)
            val categoryToAdd = selectCategoryToAdd(hashMap, it.categories!!)
            if (categoryToAdd != "") {
                hashMap[categoryToAdd]?.objects!!.add(CollectionObjectTour.createFromTourInstance(it))
            }
        }.collect(Collectors.toList())

        //Add places to hashTable
        placesFound.forEach {
            val categoryToAdd = selectCategoryToAdd(hashMap, it.categories!!)
            if (categoryToAdd != "") {
                val place = CollectionObjectPlace.createFromPlaceInstance(it)
                place.scheduleState = workingScheduleController.interpretScheduleState(place.id!!)
                hashMap[categoryToAdd]?.objects!!.add(place)
            }
        }

        //Matching recommendations
        val recommendationsFound = recommendationService.matchRecommendationsByLocation(
                locationType = searchRequest!!.exploreLocation.type,
                location = searchRequest.exploreLocation.location,
                latLng = if (latLng.isNotEmpty()) latLng.split(',') else ArrayList(),
                range = 50.0
        )
        recommendationsFound.forEach { recommendation ->
            recommendationController.extendRecommendation(recommendation)
        }

        val collections = LogicalPageList<ObjectCollection>()
        collections.addAll(recommendationsFound)
        hashMap.forEach {
            if (it.value.objects?.size!! > 0) {
                collections.add(it.value)
            }
        }

        //Extending places only to required collection
        val requiredCollection = collections.getPage(p, s);
        requiredCollection.list.forEach {
            it.objects?.forEach {
                if (it is CollectionObjectPlace) {
                    extendPlace(it)
                }
            }
        }


        return collections.getPage(p, s)
    }


    private fun selectCategoryToAdd(hashMap: HashMap<String, ObjectCollection>, categories: List<Category>): String {
        var count = Integer.MAX_VALUE
        var target = ""
        if (categories.isNotEmpty()) {
            categories.forEach {
                val count1 = hashMap[it.name]?.objects?.size!!
                if (count1 < count && count1 < 5) {
                    target = it.name!!
                    count = count1
                }
            }
        }

        return target
    }


    @GetMapping("/nearby")
    @PreAuthorize("hasAuthority('explore:nearby')")
    fun findPlacesNearby(@RequestParam(name = "p") placeId: Int): MiscellaneousCollection {
        //Selecting the place by ID to find coordinates
        val place = placeService.selectById(placeId)

        val placesNearby = placeService.selectPlacesInRadius(
                latitude = place.latitude!!.toDouble(),
                longitude = place.longitude!!.toDouble(),
                range = 10.0,
                limit = 5)

        //Removing the place whose id was provided
        placesNearby.removeAll { it.placeId == place.placeId }
        //Mapping photos and other stuff to place object
        extendPlaces(placesNearby)
        //Returning nearby places in the form of ObjectCollection
        return MiscellaneousCollection("Places nearby", null, placesNearby.map { CollectionObjectPlace.createFromPlaceInstance(it) }.toMutableList())
    }

    @GetMapping("/relatedTours")
    @PreAuthorize("hasAuthority('explore:tours_related')")
    fun findToursWithPlaces(@RequestParam(name = "p") placeId: Int): MiscellaneousCollection {

        //Selecting tours that include given placeId
        val tourIds = tourService.findToursRelatedWithPlace(placeId)
        val tours = ArrayList<Tour>()

        //Getting tours that match tour id
        tourIds.forEach {
            val tour = tourController.tourOverviewById(it)
            tours.add(tour)
        }

        //Returning nearby places in the form of ObjectCollection
        return MiscellaneousCollection("Tours with this place", null, tours.map { CollectionObjectTour.createFromTourInstance(it) }.toMutableList())
    }


}