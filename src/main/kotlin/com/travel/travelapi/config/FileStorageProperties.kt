package com.travel.travelapi.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "file")
class FileStorageProperties {
    var uploadDir: String? = "files/"

}