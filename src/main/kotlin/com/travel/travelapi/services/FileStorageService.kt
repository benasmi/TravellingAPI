package com.travel.travelapi.services

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Directory
import com.drew.metadata.MetadataException
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.jpeg.JpegDirectory
import com.travel.travelapi.config.FileStorageProperties
import com.travel.travelapi.exceptions.FileStorageException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import javax.imageio.ImageIO
import javaxt.io.Image
import java.io.*


@Service
class FileStorageService @Autowired constructor(fileStorageProperties: FileStorageProperties) {

    private val fileStorageLocation: Path = Paths.get(fileStorageProperties.uploadDir!!)
            .toAbsolutePath().normalize()


    @Throws(java.lang.Exception::class)
    fun transformImage(image: BufferedImage, transform: AffineTransform?): BufferedImage? {
        val op = AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC)
        var destinationImage = op.createCompatibleDestImage(image, if (image.type == BufferedImage.TYPE_BYTE_GRAY) image.colorModel else null)
        val g: Graphics2D = destinationImage.createGraphics()
        g.setBackground(Color.WHITE)
        g.clearRect(0, 0, destinationImage.width, destinationImage.height)
        destinationImage = op.filter(image, destinationImage)
        return destinationImage
    }

    @Throws(IOException::class, MetadataException::class, ImageProcessingException::class)
    fun fixExifOrientation(imageFile: MultipartFile): BufferedImage {
        val metadata = ImageMetadataReader.readMetadata(imageFile.inputStream)
        val directory: Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        val jpegDirectory: JpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory::class.java)
        var orientation = 1
        try {
            orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION)
        } catch (me: MetadataException) {

        }
        val width = jpegDirectory.imageWidth
        val height = jpegDirectory.imageHeight

        val t = AffineTransform()

        when (orientation) {
            1 -> {
            }
            2 -> {
                t.scale(-1.0, 1.0)
                t.translate(-width.toDouble(), 0.0)
            }
            3 -> {
                t.translate(width.toDouble(), height.toDouble())
                t.rotate(Math.PI)
            }
            4 -> {
                t.scale(1.0, -1.0)
                t.translate(0.0, -height.toDouble())
            }
            5 -> {
                t.rotate(-Math.PI / 2)
                t.scale(-1.0, 1.0)
            }
            6 -> {
                t.translate(height.toDouble(), 0.0)
                t.rotate(Math.PI / 2)
            }
            7 -> {
                t.scale(-1.0, 1.0)
                t.translate(-height.toDouble(), 0.0)
                t.translate(0.0, width.toDouble())
                t.rotate(3 * Math.PI / 2)
            }
            8 -> {
                t.translate(0.0, width.toDouble())
                t.rotate(3 * Math.PI / 2)
            }
        }
        val buffImage = ImageIO.read(imageFile.inputStream)!!

        return transformImage(buffImage, t)!!
//        return image
    }


    fun storeImageFile(image: MultipartFile): String{
        val extension = image.originalFilename!!.substring(image.originalFilename!!.lastIndexOf(".") + 1)
        val allowedExtensions = arrayOf("jpg", "png", "bmp", "jpeg", "JPEG", "BMP", "PNG", "JPG")
        if(!allowedExtensions.contains(extension))
            throw FileStorageException("Invalid file extension. Allowed extensions: $allowedExtensions")
        val generatedName = generateUniqueFileName() + '.' + extension
        val targetLocation: Path = fileStorageLocation.resolve(generatedName)

        val imageParsed = Image(image.inputStream)

        imageParsed.rotate()

        imageParsed.setOutputQuality(100.0)
        imageParsed.saveAs(targetLocation.toFile())

//        val imageFixed = fixExifOrientation(image)

//        val os = ByteArrayOutputStream()
//        ImageIO.write(imageParsed.bufferedImage, extension, os) // Passing: ​(RenderedImage im, String formatName, OutputStream output)

//        val inputStream: InputStream = ByteArrayInputStream(os.toByteArray())

//        Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
//        ImageIO.write(imageParsed.bufferedImage, extension, File("C:\\Users\\dargi\\Documents\\stuff\\TravellingAPI\\test.jpg"))
        //        val bufferedImage = ImageIO.read(image.inputStream)
//        normalizeImageExifOrientation(image);
//        Thumbnails.of(bufferedImage).size(bufferedImage.width, bufferedImage.height).toFile(targetLocation.toString())

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