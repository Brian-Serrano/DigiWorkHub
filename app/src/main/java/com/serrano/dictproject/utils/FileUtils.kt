package com.serrano.dictproject.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime

/**
 * Utility class for processing files, images, base64 encoded strings and uris
 */
object FileUtils {

    /**
     * Convert base64 encoded string image to ImageBitmap object that can be used by Icon composables
     *
     * @param[encodedImage] The base 64 encoded string image
     *
     * @return ImageBitmap Object
     */
    fun encodedStringToImage(encodedImage: String): ImageBitmap {
        val decodedBytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size).asImageBitmap()
    }

    /**
     * Convert base64 encoded string to Uri Object
     *
     * @param[encodedImage] The base64 encoded string to convert
     * @param[context] Application Context
     *
     * @return Uri Object or null
     */
    fun encodedStringToUri(encodedImage: String, context: Context): Uri? {
        val decodedBytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        val outputFile = File(context.cacheDir, "temp")
        return try {
            FileOutputStream(outputFile).use {
                it.write(decodedBytes)
            }
            val uri = Uri.fromFile(outputFile)

            if (outputFile.exists()) {
                outputFile.delete()
            }
            uri
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convert Uri Object to base64 encoded string image
     *
     * @param[imageUri] The Uri Object to convert
     * @param[context] Application Context
     *
     * @return base64 encoded string image
     */
    fun uriToEncodedString(imageUri: Uri, context: Context): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val inputData = MiscUtils.getBytes(inputStream ?: throw IOException())
        return Base64.encodeToString(inputData, Base64.DEFAULT)
    }

    /**
     * Convert Uri Object to Image that can be used by Icon composables
     *
     * @param[imageUri] The Image Uri to Convert
     * @param[context] Application Context
     *
     * @return ImageBitmap Object
     */
    fun imageUriToImage(imageUri: Uri?, context: Context): ImageBitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            MiscUtils.scaleImage(ImageDecoder.decodeBitmap(source)).asImageBitmap()
        } else {
            MiscUtils.scaleImage(MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)).asImageBitmap()
        }
    }

    /**
     * Convert the Uri Object to a File Object. Used in uploading attachment.
     *
     * @param[context] Application Context
     * @param[uri] The Uri to convert
     *
     * @return File Object
     */
    fun getFileFromUri(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, MiscUtils.getFileNameFromUri(context, uri))
        return context.contentResolver.openInputStream(uri).use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
            file
        }
    }

    /**
     * Convert ImageBitmap Object to File. Used for sending it in backend through MultiPartBody.Part
     *
     * @param[imageBitmap] The image to convert
     * @param[context] Application Context. Needed because the image will temporarily stored in internal storage
     *
     * @return File Object
     */
    fun bitmapToFile(imageBitmap: ImageBitmap, context: Context): File {
        val bitmap = imageBitmap.asAndroidBitmap()
        val file = File(context.cacheDir, LocalDateTime.now().toString() + ".png")
        return FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            outputStream.flush()
            file
        }
    }

    /**
     * Convert ImageBitmap Object to base64 encoded string image. Used in settings when the user changes image
     *
     * @param[imageBitmap] The image to convert
     *
     * @return base64 encoded string image
     */
    fun imageToEncodedString(imageBitmap: ImageBitmap): String {
        val bitmap = imageBitmap.asAndroidBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}