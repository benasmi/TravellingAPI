package com.travel.travelapi.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
class FailedApiRequestException(override val message: String): RuntimeException(message)