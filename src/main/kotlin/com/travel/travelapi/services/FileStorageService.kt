package com.travel.travelapi.services

import com.travel.travelapi.config.FileStorageProperties
import com.travel.travelapi.exceptions.FileStorageException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Service
class FileStorageService @Autowired constructor(fileStorageProperties: FileStorageProperties) {

    private val fileStorageLocation: Path = Paths.get(fileStorageProperties.uploadDir!!)
            .toAbsolutePath().normalize()

    fun storeFile(file: MultipartFile, name: String): String {
        // Normalize file name
        val fileName: String = StringUtils.cleanPath(file.name)
        return try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw FileStorageException("Sorry! Filename contains invalid path sequence $fileName")
            }

            // Copy file to the target location (Replacing existing file with the same name)
            val targetLocation: Path = fileStorageLocation.resolve(name)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            fileName
        } catch (ex: IOException) {
            println(ex.toString())
            throw FileStorageException("Could not store file $fileName. Please try again!")
        }
    }

    fun loadFileAsResource(fileName: String): Resource {
        return try {
            val filePath: Path = fileStorageLocation.resolve(fileName).normalize()
            val resource: Resource = UrlResource(filePath.toUri())
            if (resource.exists()) {
                resource
            } else {
                throw FileStorageException("File not found $fileName")
            }
        } catch (ex: MalformedURLException) {
            println(ex.toString())
            throw FileStorageException("File not found $fileName")
        }
    }

    init {
        try {
            Files.createDirectories(fileStorageLocation)
        } catch (ex: Exception) {
            println(ex.toString())
            throw FileStorageException("Could not create the directory where the uploaded files will be stored.")
        }
    }
}