package com.travel.travelapi.controllers

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.InvalidUserDataException
import com.travel.travelapi.models.Review
import com.travel.travelapi.models.ReviewType
import com.travel.travelapi.services.ReviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/review")
class ReviewController(@Autowired private val reviewService: ReviewService){


    /**
     * Methods inserts unique review for an object of type PLACE(1) or TOUR(2)
     *
     * !Default type for review is PLACE(1)
     * @see ReviewType
     *
     * @return inserted review row id
     */
    @PostMapping("/insert")
    fun insertReview(@RequestBody review: Review): Review{

        //Getting the authenticated user
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        review.userId = principal.id?.toInt()

        //Check if this user already left a review for this object
        if(getUserPlaceReview(review.objectId!!) != null){
            throw InvalidUserDataException("Review was left for this place by this user already!")
        }

        reviewService.insertReview(review)

        return review
    }

    /**
     * Method returns review of an object left by specific user
     * @param objectId of an object
     * @param reviewType is either 1-Place or 2-Tour
     *
     * @return review if found or null if not.
     */
    @GetMapping("/userPlace")
    fun getUserPlaceReview(@RequestParam(name="p") objectId: Int,
                           @RequestParam(name="type", defaultValue = "1") reviewType: Int = ReviewType.PLACE.reviewType): Review?{
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails
        return reviewService.getUserPlaceReview(objectId, principal.id!!.toInt(), reviewType)
    }

    /**
     * Method to up vote a review
     * @param reviewId of an object
     */
    @GetMapping("/upvote")
    fun upVoteReview(@RequestParam("id") reviewId: Int){
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Check if user didn't up vote already
        if(reviewService.alreadyUpVoted(reviewId, principal.id!!.toInt())){
            throw InvalidUserDataException("This review was already up voted by this user!")
        }

        reviewService.upVoteReview(reviewId,principal.id!!.toInt())
    }


    /**
     * Method to down vote a review
     * @param reviewId of an object
     */
    @GetMapping("/downvote")
    fun downVoteReview(@RequestParam("id") reviewId: Int){
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Check if there is an up vote to be down voted
        if(!reviewService.alreadyUpVoted(reviewId, principal.id!!.toInt())){
            throw InvalidUserDataException("This review was not up voted by this user!")
        }

        reviewService.downVoteReview(reviewId,principal.id!!.toInt())
    }

    /**
     * Method to report a review
     * @param reviewId of an object
     */
    @GetMapping("/report")
    fun reportReview(@RequestParam("id") reviewId: Int){
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        //Check if this user reported already
        if(reviewService.alreadyReported(reviewId, principal.id!!.toInt())){
            throw InvalidUserDataException("This review was already reported by this user!")
        }

        reviewService.reportReview(reviewId,principal.id!!.toInt())
    }

    /**
     * Method returns physically paged reviews for given objectId and type
     *
     * @param id of an object
     * @param type of an object. PLACE or TOUR
     * @param p page
     * @param s page size
     *
     * @see ReviewType
     */
    @GetMapping("/object")
    fun getAllReviews(@RequestParam("id") id: Int,
                      @RequestParam(name="type", defaultValue = "1") type: Int = ReviewType.PLACE.reviewType,
                      @RequestParam(name="p", defaultValue = "1") p: Int,
                      @RequestParam(name="s", defaultValue = "10") s: Int): PageInfo<Review>{

        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        PageHelper.startPage<Review>(p,s)
        val reviews = reviewService.getAllObjectReviews(id,type, principal.id!!.toInt())

        reviews.forEach{
            it.upVotes = reviewService.getReviewUpVotesCount(it.id!!)
            it.displayName = reviewService.getDisplayName(it.id)
            it.userPhoto = reviewService.getDisplayPhoto(it.id)
            it.userUpVoted = reviewService.alreadyUpVoted(it.id, principal.id!!.toInt())
            it.gender = reviewService.getGender(it.id)
            it.userReported = reviewService.alreadyReported(it.id, principal.id!!.toInt())
        }

        return PageInfo(reviews)

    }
}