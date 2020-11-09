package com.travel.travelapi.models

import com.fasterxml.jackson.annotation.JsonInclude
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Category(val name: String? = null,
               val categoryId: Int? = null){
}

data class AbstractionCategory(val name: String? = null,
                               val categoryId: Int? = null,
                               val mappedCategories: List<Category>? = null)