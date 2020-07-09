package com.travel.travelapi.validation

import javax.validation.Constraint
import kotlin.reflect.KClass


@MustBeDocumented
@Constraint(validatedBy = [PasswordConstraintValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.LOCAL_VARIABLE)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ValidPassword(val message: String = "Invalid Password",
                               val groups: Array<KClass<*>> = [],
                               val payload: Array<KClass<out Any>> = [])