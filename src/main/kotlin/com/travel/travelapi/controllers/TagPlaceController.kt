package com.travel.travelapi.controllers

import com.travel.travelapi.models.Tag
import com.travel.travelapi.models.TagPlace
import com.travel.travelapi.services.TagPlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tagsplace")
class TagPlaceController (@Autowired private val tagPlaceService: TagPlaceService){

    /**
     * Get tags for a place
     * @param placeId of a place
     */
    fun getTagsById(placeId: Int) : List<Tag>{
        return tagPlaceService.selectTagsById(placeId)
    }

    /**
     * Map tags to a place
     */
    @RequestMapping("/insert")
    fun insertTagForPlace(@RequestBody tagPlaces: List<TagPlace>){
        for(t: TagPlace in tagPlaces)
            tagPlaceService.insertTagForPlace(t)
    }

    /**
     * Update place tags
     */
    @RequestMapping("/update")
    fun updateTagForPlace(@RequestBody tagPlaces: List<Tag>, @RequestParam(name="p") id: Int){
        tagPlaceService.deleteTagForPlaceById(id)

        val tags = ArrayList<TagPlace>()
        for(t: Tag in tagPlaces)
            tags.add(TagPlace(t.tagId,id))

        insertTagForPlace(tags)

    }

    @PostMapping("/delete")
    fun deleteTagForPlace(@RequestBody tagPlaces: List<TagPlace>){
        for(t: TagPlace in tagPlaces)
            tagPlaceService.deleteTagForPlace(t)
    }


}