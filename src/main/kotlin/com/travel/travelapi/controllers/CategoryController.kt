package com.travel.travelapi.controllers

import com.travel.travelapi.models.AbstractionCategory
import com.travel.travelapi.models.Category
import com.travel.travelapi.services.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import kotlin.collections.ArrayList


@RestController
@RequestMapping("/categories")
class CategoryController(@Autowired private val categoryService: CategoryService) {


    /**
     * @return all categories
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('category:read')")
    fun getAllCategories() : List<Category>{
        return categoryService.selectAllCategories()
    }

    /**
     * @return all abstraction categories
     */
    @GetMapping("/all/abstracted")
    @PreAuthorize("hasAuthority('category:read')")
    fun getAllAbstractionCategories() : List<AbstractionCategory>{
        val cats = categoryService.selectAllAbstractionCategories()
        val abstractedCategories = ArrayList<AbstractionCategory>()
        cats.forEach {
            val mappedCategories = categoryService.getCategoriesForAbstracted(it.categoryId!!)
            abstractedCategories.add(AbstractionCategory(it.name, it.categoryId, mappedCategories))
        }
        return abstractedCategories
    }

    /**
     * @return all abstraction categories
     */
    @PostMapping("/update/abstracted")
    @PreAuthorize("hasAuthority('category:write')")
    fun updateAbstractionCategories(@RequestBody abstractionCategory: AbstractionCategory){
        categoryService.updateAbstractionCategory(abstractionCategory)
    }


    /**
     * @return all abstraction categories
     */
    @GetMapping("/abstracted/left")
    @PreAuthorize("hasAuthority('category:write')")
    fun getAbstractionsCatsLeftOvers() : List<Category>{
        return categoryService.getAbstractionCategoryLeftOver()
    }

    /**
     * Insert category
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('category:write')")
    fun insertCategory(@RequestBody categories: List<Category>): List<Int>{
        val inserted: ArrayList<Int> = ArrayList()
        for(c: Category in categories){
            categoryService.insertCategory(c)
            inserted.add(c.categoryId!!)
        }
        return inserted
    }

    /**
     * Delete categories
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('category:write')")
    fun deleteCategory(@RequestBody categories: List<Category>){
        for(c: Category in categories)
            categoryService.deleteCategory(c)
    }

    /**
     * Delete categories
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('category:write')")
    fun updateCategory(@RequestBody categories: List<Category>){
        for(c: Category in categories)
            categoryService.updateCategory(c)
    }

}