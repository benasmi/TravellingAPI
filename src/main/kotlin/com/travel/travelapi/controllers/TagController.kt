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
     * Delete tags
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('tag:write')")
    fun updateTag(@RequestBody tags: List<Tag>){
        for(c: Tag in tags)
            tagService.updateTag(c)
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