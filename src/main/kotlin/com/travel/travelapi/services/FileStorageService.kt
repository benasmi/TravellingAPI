package com.travel.travelapi.services

import com.travel.travelapi.config.FileStorageProperties
import com.travel.travelapi.exceptions.FileStorageException
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*


@Service
class FileStorageService @Autowired constructor(fileStorageProperties: FileStorageProperties) {

    private val fileStorageLocation: Path = Paths.get(fileStorageProperties.uploadDir!!)
            .toAbsolutePath().normalize()

    fun storeImageFile(image: MultipartFile): String{
        val extension = image.originalFilename!!.substring(image.originalFilename!!.lastIndexOf(".") + 1)
        val allowedExtensions = arrayOf("jpg", "png", "bmp", "jpeg", "JPEG", "BMP", "PNG", "JPG")
        if(!allowedExtensions.contains(extension))
            throw FileStorageException("Invalid file extension. Allowed extensions: $allowedExtensions")
        val generatedName = generateUniqueFileName() + '.' + extension
        val targetLocation: Path = fileStorageLocation.resolve(generatedName)
        Thumbnails.of(image.inputStream).scale(1.0).toFile(targetLocation.toString())

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/photo/view")
                .toUriString() + "?photoreference=$generatedName"

    }

    fun generateUniqueFileName(): String? {
        var filename = ""
        val millis = System.currentTimeMillis()
        var datetime: String = Date().toGMTString()
        datetime = datetime.replace(" ", "")
        datetime = datetime.replace(":", "")
        val rndchars = UUID.randomUUID().toString()
        filename = rndchars + "_" + datetime + "_" + millis
        return filename
    }

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