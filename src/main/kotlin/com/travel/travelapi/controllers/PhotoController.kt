package com.travel.travelapi.controllers

import com.travel.travelapi.exceptions.FileStorageException
import com.travel.travelapi.models.Photo
import com.travel.travelapi.services.FileStorageService
import com.travel.travelapi.services.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.util.*
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType


@RestController
@RequestMapping("/photo")
class PhotoController(
        @Autowired private val photoService: PhotoService,
        @Autowired val fileStorageService: FileStorageService) {

    /**
     * Insert photo
     */
    @PostMapping("/insert")
    fun insertPhoto(@RequestBody photos : List<Photo>): List<Int>{
        val inserted = ArrayList<Int>()
        for(p: Photo in photos){
            photoService.insertPhoto(p)
            inserted.add(p.photoId!!)
        }
        return inserted
    }

    fun saveImageFile(image: MultipartFile): String{
        val extension = image.originalFilename!!.substring(image.originalFilename!!.lastIndexOf(".") + 1)
        val allowedExtensions = arrayOf("PNG", "jpg", "png", "bmp")
        if(!allowedExtensions.contains(extension))
            throw FileStorageException("Invalid file extension. Allowed extensions: $allowedExtensions")
        val generatedName = generateUniqueFileName() + '.' + extension
        fileStorageService.storeFile(image, generatedName)
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/photo/")
                .path(generatedName)
                .toUriString()

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

    @GetMapping("/{fileName:.+}")
    fun serveFile(@PathVariable fileName: String?, request: HttpServletRequest): ResponseEntity<Resource?>? {
        // Load file as Resource
        val resource: Resource = fileStorageService.loadFileAsResource(fileName!!)

        // Try to determine file's content type
        var contentType: String? = null
        try {
            contentType = request.servletContext.getMimeType(resource.getFile().getAbsolutePath())
        } catch (ex: IOException) {
            println("Could not determine file type.")
        }

        // Fallback to the default content type if type could not be determined
//        if (contentType == null) {
//            contentType = "application/octet-stream"
//        }
        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename().toString() + "\"")
                .body<Resource?>(resource)
    }


    @PostMapping("/upload")
    fun insertPhoto(@RequestBody image: MultipartFile): Photo{
        val linkToImage = saveImageFile(image)
        val photo = Photo(url =  linkToImage)
        photoService.insertPhoto(photo)
        return photo
    }



}