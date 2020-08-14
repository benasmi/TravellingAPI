package com.travel.travelapi.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
class MissingHeaderDataException(override val message: String): RuntimeException(message)