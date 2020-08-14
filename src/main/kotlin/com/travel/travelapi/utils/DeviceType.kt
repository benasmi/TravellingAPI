package com.travel.travelapi.utils

import com.travel.travelapi.exceptions.MissingHeaderDataException
import javax.servlet.http.HttpServletRequest

class DeviceType {
    companion object{
        fun getDevice(request: HttpServletRequest): String{
            val deviceHeader = request.getHeader("device")
            if (deviceHeader.isNullOrEmpty()) {
                return "web"
            }
            return deviceHeader
        }
    }
}