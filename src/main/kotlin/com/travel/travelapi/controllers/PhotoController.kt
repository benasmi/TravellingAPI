package com.travel.travelapi.controllers

import com.travel.travelapi.auth.TravelUserDetails
import com.travel.travelapi.exceptions.FileStorageException
import com.travel.travelapi.models.Photo
import com.travel.travelapi.services.FileStorageService
import com.travel.travelapi.services.PhotoService
import net.coobird.thumbnailator.Thumbnails
import org.imgscalr.Scalr.resize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.ParseException
import java.util.*
import java.util.UUID
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/photo")
class PhotoController(
        @Autowired private val photoService: PhotoService,
        @Autowired val fileStorageService: FileStorageService) {

    /**
     * Insert photo
     */
//    @PostMapping("/insert")
//    fun insertPhoto(@RequestBody photos : List<Photo>): List<Int>{
//        val inserted = ArrayList<Int>()
//        for(p: Photo in photos){
//            photoService.insertPhoto(p)
//            inserted.add(p.photoId!!)
//        }
//        return inserted
//    }


    @GetMapping("/view")
//    @PreAuthorize("hasAuthority('photo:view')")
    fun serveFile(@RequestParam(name = "size", defaultValue = "500") size: String,
                  @RequestParam(name ="photoreference") fileName: String?, request: HttpServletRequest): ResponseEntity<ByteArray?>? {
        // Load file as Resource
        val resource: Resource = fileStorageService.loadFileAsResource(fileName!!)

            var fileSize = 500
            if(!size.equals("full")){
                try {
                    fileSize = Integer.parseInt(size)
                }
                catch (e: ParseException) {
                    println("Size is not an integer")
                }
            }
            val image: BufferedImage = if(!size.equals("full"))
                resize(ImageIO.read(resource.file), fileSize) else
                ImageIO.read(resource.file)

            val baos = ByteArrayOutputStream()
            ImageIO.write(image, "jpg", baos)
            baos.flush()
            val imageInByte: ByteArray = baos.toByteArray()
            baos.close()

        // Try to determine file's content type
        var contentType: String? = null
        try {
            contentType = request.servletContext.getMimeType(resource.file.getAbsolutePath())
        } catch (ex: IOException) {
            println("Could not determine file type.")
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType!!))
                .body(imageInByte)
    }


    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('photo:upload')")
    fun insertPhoto(@RequestBody image: MultipartFile): Photo{
        val principal = SecurityContextHolder.getContext().authentication.principal as TravelUserDetails

        val linkToImage = fileStorageService.storeImageFile(image)
        val photo = Photo(url =  linkToImage, userId = principal.id?.toInt())

        photoService.insertPhoto(photo)
        return photo
    }



}