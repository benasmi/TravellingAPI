package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.exceptions.InvalidParamsException
import com.travel.travelapi.models.*
import com.travel.travelapi.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/explore")
class ExplorePageController(
        @Autowired private val explorePageService: ExplorePageService,
        @Autowired private val recommendationController: RecommendationController,
        @Autowired private val photoPlaceService: PhotoPlaceService,
        @Autowired private val placeService: PlaceService,
        @Autowired private val placeController: PlaceController,
        @Autowired private val tourController: TourController,
        @Autowired private val tourService: TourService,
        @Autowired private val recommendationService: RecommendationService,
        @Autowired private val categoryPlaceService: CategoryPlaceService
){

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('explore:update')")
    fun update(@RequestBody recommendations: List<Int>){
        if(recommendations.distinct().count() == recommendations.count()){
            //No duplicate recommendations
            explorePageService.update(recommendations)
        }else{
            throw InvalidParamsException("The explore page cannot have any duplicate recommendations")
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('explore:read')")
    fun getExplorePage(
            @RequestParam(required = false, defaultValue = "1") p: Int,
            @RequestParam(required = false, defaultValue = "10") s: Int
    ): PageInfo<Recommendation>{
        PageHelper.startPage<Recommendation>(p, s)

        val recommendations = explorePageService.selectAll()

        for(recommendation in recommendations)
            recommendationController.extendRecommendation(recommendation)

        return PageInfo(recommendations)
    }


    @GetMapping("/search")
    fun search(@RequestParam keyword: String): ClientSearchResult {
        if(keyword == "")
            return ClientSearchResult(arrayListOf(), arrayListOf(), arrayListOf())

        val places = placeService.searchClient(keyword)
        val tours = getToursAssociatedWithPlaces(places)
        for(place in places){
            val photos = photoPlaceService.selectPhotosById(place.placeId!!)
            place.photos = if(photos.count() > 0) arrayListOf(photos[0]) else arrayListOf()

        }
        val test = explorePageService.matchSearch(keyword)
        return ClientSearchResult(places, tours, test)
    }

    fun getToursAssociatedWithPlaces(places: List<PlaceLocal>): List<Tour>{
        val tours = arrayListOf<Tour>()
        for(place in places){
            val tourIds = explorePageService.selectToursForPlace(place.placeId!!)
            for(tour in tourIds)
                tours.add(tourController.tourOverviewById(tour.tourId!!))
        }
        return tours.distinctBy { tour -> tour.tourId }
    }

    data class ExploreLocation(val location: String,
    val type: String){
        fun valid(): Boolean{
            return type == "city" || type == "country" || type == "municipality" || type == "county"
        }
    }

    @PostMapping("/location")
    fun getByLocation(@RequestBody exploreLocationRequest: ExploreLocation): List<ObjectCollection>{
        if(!exploreLocationRequest.valid())
            throw InvalidParamsException("Type specified is invalid")
        if(exploreLocationRequest.location == "")
            throw InvalidParamsException("No location specified")

        var categoriesAll = arrayListOf<Category>()

        //Matching places
        var placesFound = placeService.matchPlacesByLocation(exploreLocationRequest.location, exploreLocationRequest.type)
        placesFound.map { place ->
            val photos = photoPlaceService.selectPhotosById(place.placeId!!)
            place.photos = if(photos.count() > 0) arrayListOf(photos[0]) else arrayListOf()
            place.categories = categoryPlaceService.selectByPlaceId(place.placeId)
            categoriesAll.addAll(place.categories!!)
        }

        //Matching tours
        var tours = getToursAssociatedWithPlaces(placesFound)
        tours.map { tour ->
            tour.categories = tourService.getCategoriesForTour(tour.tourId!!)
            categoriesAll.addAll(tour.categories!!)
        }

        //Matching recommendations
        val recommendations = arrayListOf<Recommendation>()
        for(place in placesFound)
            recommendations.addAll(recommendationService.matchRecommendationsByLocation(exploreLocationRequest.type, exploreLocationRequest.location))
            recommendations.forEach{recommendation ->
            recommendationController.extendRecommendation(recommendation)
        }

        val collections = arrayListOf<ObjectCollection>()

        //Adding recommendations to the location results object
        collections.addAll(recommendations.distinctBy { it.id })

        while(true){

            //Finding the category, which is contained by most tours/places in this search
            val mostPopularCategory = categoriesAll.groupingBy { it.categoryId }.eachCount().maxBy { it.value }

            mostPopularCategory ?: break

            //We do not want to map places/tours to categories if there are less than a certain amount of objects mapped to a category
            if(mostPopularCategory.value < 3)
                break;

            //Getting the most popular categorie's name and ID
            val categoryPopular = categoriesAll.filter { it.categoryId == mostPopularCategory.key }[0]

            val collectionsByCategories = arrayListOf<CollectionObject>()

            //Adding places, that contain the most popular tag
            collectionsByCategories.addAll(placesFound.filter{ place ->
                place.categories!!.any{ category -> category.categoryId == mostPopularCategory.key}}
                    .map { CollectionObjectPlace.createFromPlaceInstance(it) })

            //Adding tours, that contain the most popular tag
            collectionsByCategories.addAll(tours.filter{ tour ->
                tour.categories!!.any{ category -> category.categoryId == mostPopularCategory.key}}
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
        otherPlaces.addAll(placesFound.map{
            CollectionObjectPlace.createFromPlaceInstance(it)
        })
        collections.add(MiscellaneousCollection(objects = otherPlaces, name="Other places", subtitle=""))

        //Adding tours to the "others" section, since they did not belong to any popular tag
        val otherTours = arrayListOf<CollectionObject>()
        otherTours.addAll(tours.map { CollectionObjectTour.createFromTourInstance(it) })
        collections.add(MiscellaneousCollection(objects = otherTours, name="Other tours", subtitle=""))

        return collections
    }

}