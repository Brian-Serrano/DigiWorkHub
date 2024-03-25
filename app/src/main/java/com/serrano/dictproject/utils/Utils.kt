package com.serrano.dictproject.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.auth0.jwt.JWT
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.round


object Utils {

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")

    // Date and date string
    fun dateTimeToDateString(date: LocalDateTime): String {
        return LocalDate.of(date.year, date.month, date.dayOfMonth)
            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }

    fun dateTimeToDateTimeString(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a"))
    }

    fun dateTimeToTimeString(date: LocalDateTime): String {
        return LocalTime.of(date.hour, date.minute)
            .format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    fun dateTimeToDate(date: LocalDateTime): LocalDate {
        return LocalDate.of(date.year, date.month, date.dayOfMonth)
    }

    fun dateToDateString(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }

    // Uri, image, file, base64 string
    fun encodedStringToImage(encodedImage: String): ImageBitmap {
        val decodedBytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size).asImageBitmap()
    }

    fun encodedStringToUri(encodedImage: String, context: Context): Uri? {
        val decodedBytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        val outputFile = File(context.cacheDir, "temp")
        try {
            FileOutputStream(outputFile).use {
                it.write(decodedBytes)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return Uri.fromFile(outputFile)
    }

    fun uriToEncodedString(imageUri: Uri, context: Context): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val inputData = getBytes(inputStream ?: throw IOException())
        return Base64.encodeToString(inputData, Base64.DEFAULT)
    }

    fun imageUriToImage(imageUri: Uri?, context: Context): ImageBitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            scaleImage(ImageDecoder.decodeBitmap(source)).asImageBitmap()
        } else {
            scaleImage(MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)).asImageBitmap()
        }
    }

    fun getFileFromUri(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, getFileNameFromUri(context, uri))
        return context.contentResolver.openInputStream(uri).use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
            file
        }
    }

    fun bitmapToFile(imageBitmap: ImageBitmap, context: Context): File {
        val bitmap = imageBitmap.asAndroidBitmap()
        val file = File(context.filesDir, LocalDateTime.now().toString() + ".png")
        return FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            outputStream.flush()
            file
        }
    }

    fun imageToEncodedString(imageBitmap: ImageBitmap): String {
        val bitmap = imageBitmap.asAndroidBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Others
    @Throws(IOException::class)
    private fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    fun getCalendarData(calendarTabIdx: Int, tasks: List<TaskPart>): List<Calendar> {
        val currentDate = LocalDate.now().plusMonths(calendarTabIdx.toLong())
        val mappedTasks = tasks
            .filter { currentDate.year == it.due.year && currentDate.monthValue == it.due.monthValue }
        val firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth())
        val firstDayOfMonthView = firstDayOfMonth.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
        val lastDayOfMonthView = lastDayOfMonth.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
        val numOfDays = ChronoUnit.DAYS.between(firstDayOfMonthView, lastDayOfMonthView)
        val dates = Stream.iterate(firstDayOfMonthView) {
            it.plusDays(1)
        }.limit(numOfDays)
            .collect(Collectors.toList())
        return dates.map { date ->
            Calendar(
                date = date,
                calendarTasks = mappedTasks
                    .filter { LocalDate.of(it.due.year, it.due.month, it.due.dayOfMonth) == date }
                    .map { CalendarTask(it.taskId, it.title) }
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveFileToDevice(context: Context, body: ResponseBody, fileName: String, extension: String) {
        val values = ContentValues().apply {
            put(
                MediaStore.Downloads.DISPLAY_NAME,
                fileName + "_" + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd_MM_yyyy_hh_mm_ss")
                ) + "." + extension
            )
        }
        val uri = context.contentResolver.insert(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            values
        )
        context.contentResolver.openOutputStream(uri!!).use {
            it?.write(body.bytes())
        }
        values.clear()
    }

    private fun scaleImage(image: Bitmap, imageSize: Float = 200f): Bitmap {
        val ratio = max(imageSize / image.width, imageSize / image.height)
        val newWidth = round(ratio * image.width).toInt()
        val newHeight = round(ratio * image.height).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
        return Bitmap.createBitmap(scaledBitmap, 0, 0, 199, 199)
    }

    private fun checkToken(authToken: String): Boolean {
        return try { JWT.decode(authToken).expiresAt.before(Date()) } catch (e: Exception) { true }
    }

    @SuppressLint("Range")
    fun getFileNameFromUri(context: Context, uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null, null).use { cursor ->
            cursor?.moveToFirst()
            cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        } ?: ""
    }

    fun <T> sdk29AndUp(onSdk29: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            onSdk29()
        } else null
    }

    // suspend
    suspend fun checkAuthentication(
        context: Context,
        preferencesRepository: PreferencesRepository,
        apiRepository: ApiRepository
    ) {
        val preferences = preferencesRepository.getData().first()

        if (checkToken(preferences.authToken)) {
            Toast.makeText(context, "Your token is not valid, refreshing Login.", Toast.LENGTH_LONG).show()

            val authResponse = apiRepository.login(
                Login(preferences.email, preferences.password)
            )

            when (authResponse) {
                is Resource.Success -> {
                    val responseToken = authResponse.data!!.token
                    preferencesRepository.updateAuthToken(responseToken)
                    Toast.makeText(context, "Login success.", Toast.LENGTH_LONG).show()
                }
                is Resource.ClientError -> {
                    throw IllegalStateException(authResponse.clientError?.message)
                }
                is Resource.GenericError -> {
                    throw IllegalStateException(authResponse.genericError)
                }
                is Resource.ServerError -> {
                    throw IllegalStateException(authResponse.serverError?.error)
                }
            }
        }
    }
}

fun LazyGridScope.header(content: @Composable LazyGridItemScope.() -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }, content = content)
}