package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.*
import com.travel.travelapi.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

data class TypeIdPair(val id: Int, val type: String)

@RestController
@RequestMapping("/explore")
class ExplorePageController(
        @Autowired private val explorePageService: ExplorePageService,
        @Autowired private val recommendationController: RecommendationController,
        @Autowired private val photoPlaceService: PhotoPlaceService,
        @Autowired private val placeController: PlaceController,
        @Autowired private val placeService: PlaceService,
        @Autowired private val tourController: TourController,
        @Autowired private val tourService: TourService,
        @Autowired private val recommendationService: RecommendationService,
        @Autowired private val categoryPlaceService: CategoryPlaceService,
        @Autowired private val dataCollectionController: DataCollectionController
) {

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('explore:update')")
    fun update(@RequestBody recommendations: List<Int>) {
        if (recommendations.distinct().count() == recommendations.count()) {
            //No duplicate recommendations
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
                MiscellaneousCollection(objects = places.map { place -> CollectionObjectPlace.createFromPlaceInstance(place) }),
                MiscellaneousCollection(objects = tours.map { tour -> CollectionObjectTour.createFromTourInstance(tour) }),
                explorePageService.matchSearch(keyword))
    }

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

    @PostMapping("/location")
    @PreAuthorize("hasAuthority('explore:location')")
    fun getByLocation(@RequestBody(required = false) exploreLocationRequest: ExploreLocation,
                      @RequestParam(required = false, defaultValue = "") latLng: String,
                      @RequestParam(required = false, defaultValue = "0") p: Int,
                      @RequestParam(required = false, defaultValue = "10") s: Int): LogicalPage {
        if (latLng.isEmpty() && !exploreLocationRequest.valid())
            throw InvalidParamsException("Type specified is invalid")
        if (latLng.isEmpty() && exploreLocationRequest.location == "")
            throw InvalidParamsException("No location specified")

        dataCollectionController.searchedLocation(exploreLocationRequest)

        val categoriesAll = arrayListOf<Category>()

        //Matching places
        var placesFound = if (latLng.isEmpty())
            placeService.matchPlacesByLocation(exploreLocationRequest.location, exploreLocationRequest.type)
        else
            placeService.selectAllAdmin(location = latLng.split(','), range = 50.0)

        extendPlaces(placesFound)
        placesFound.forEach {
            categoriesAll.addAll(it.categories!!)
        }
        //Matching tours
        var tours = getToursAssociatedWithPlaces(placesFound)
        tours.map { tour ->
            tour.categories = tourService.getCategoriesForTour(tour.tourId!!)
            categoriesAll.addAll(tour.categories!!)
        }

        //Matching recommendations
        val recommendationsFound = recommendationService.matchRecommendationsByLocation(
                locationType = exploreLocationRequest.type,
                location = exploreLocationRequest.location,
                latLng = if (latLng.isNotEmpty()) latLng.split(',') else ArrayList(),
                range = 50.0
        )

        val recommendations = arrayListOf<Recommendation>()
        recommendations.addAll(recommendationsFound)
        recommendations.forEach { recommendation ->
            recommendationController.extendRecommendation(recommendation)
        }

        val collections = LogicalPageList<ObjectCollection>()

        //Adding recommendations to the location results object
        collections.addAll(recommendations)

        while (true) {

            //Finding the category, which is contained by most tours/places in this search
            val mostPopularCategory = categoriesAll.groupingBy { it.categoryId }.eachCount().maxBy { it.value }

            mostPopularCategory ?: break

            //We do not want to map places/tours to categories if there are less than a certain amount of objects mapped to a category
            if (mostPopularCategory.value < 2)
                break;

            //Getting the most popular category name and ID
            val categoryPopular = categoriesAll.filter { it.categoryId == mostPopularCategory.key }[0]

            val collectionsByCategories = arrayListOf<CollectionObject>()

            //Adding places, that contain the most popular category
            collectionsByCategories.addAll(placesFound.filter { place ->
                place.categories!!.any { category -> category.categoryId == mostPopularCategory.key }
            }
                    .map { CollectionObjectPlace.createFromPlaceInstance(it) })

            //Adding tours, that contain the most popular tag
            collectionsByCategories.addAll(tours.filter { tour ->
                tour.categories!!.any { category -> category.categoryId == mostPopularCategory.key }
            }
                    .map { CollectionObjectTour.createFromTourInstance(it) })

            //Adding the current location result object to the list
            collections.add(SuggestionByCategory(category = categoryPopular, objects = collectionsByCategories))

            //Removing places and tours containing the most popular tag
            placesFound = ArrayList(placesFound.filter { place -> !place.categories!!.any { category -> category.categoryId == mostPopularCategory.key } })
            tours = ArrayList(tours.filter { tour -> !tour.categories!!.any { category -> category.categoryId == mostPopularCategory.key } })

            categoriesAll.clear()
            for (place in placesFound)
                categoriesAll.addAll(place.categories?.toList() ?: listOf())
            for (tour in tours)
                categoriesAll.addAll(tour.categories?.toList() ?: listOf())
        }

        //Adding places to the "others" section, since they did not belong to any popular tag
        val otherPlaces = arrayListOf<CollectionObject>()
        otherPlaces.addAll(placesFound.map {
            CollectionObjectPlace.createFromPlaceInstance(it)
        })
        if(otherPlaces.count() > 0)
            collections.add(MiscellaneousCollection(objects = otherPlaces, name = "Other places", subtitle = ""))

        //Adding tours to the "others" section, since they did not belong to any popular tag
        val otherTours = arrayListOf<CollectionObject>()
        otherTours.addAll(tours.map { CollectionObjectTour.createFromTourInstance(it) })
        if(otherTours.count() > 0)
            collections.add(MiscellaneousCollection(objects = otherTours, name = "Other tours", subtitle = ""))


        return collections.getPage(p, s)
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
        return MiscellaneousCollection("Places nearby", null, placesNearby.map { CollectionObjectPlace.createFromPlaceInstance(it) })
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
        return MiscellaneousCollection("Tours with this place", null, tours.map { CollectionObjectTour.createFromTourInstance(it) })
    }


}