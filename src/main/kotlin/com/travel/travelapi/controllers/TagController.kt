package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.UnauthorizedException
import com.travel.travelapi.models.Category
import com.travel.travelapi.models.Tag
import com.travel.travelapi.services.PlaceService
import com.travel.travelapi.services.TagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tags")
class TagController(@Autowired private val tagService: TagService) {

    /**
     * Get all tags
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('tag:read')")
    fun getAllTags(): List<Tag>{
        return tagService.selectAllTags()
    }

    /**
     * Get all tags
     */
    @GetMapping("/featured/all")
    @PreAuthorize("hasAuthority('tag:read')")
    fun getAllTagsFeatured(): List<Tag>{
        return tagService.selectAllFeaturedTags()
    }


    /**
     * Insert tags
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('tag:write')")
    fun insertTag(@RequestBody tags: List<Tag>): List<Int>{
        val inserted = ArrayList<Int>()
        for(t: Tag in tags){
            tagService.insertTag(t)
            inserted.add(t.tagId!!)
        }
        return inserted
    }

    /**
     * Update tags
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('tag:write')")
    fun updateTag(@RequestBody tags: List<Tag>){
        for(c: Tag in tags)
            tagService.updateTag(c)
    }

    /**
     * Update tags
     */
    @PostMapping("/featured/update")
    @PreAuthorize("hasAuthority('tag:write')")
    fun updateFeaturedTags(@RequestBody ids: List<Tag>){
        tagService.updateFeaturedTags(ids)
    }



    /**
     * Delete tags
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('tag:write')")
    fun deleteTag(@RequestBody tags: List<Tag>){
        for(t: Tag in tags)
            tagService.deleteTag(t)

    }

}