package com.example.drawingapp.network

import android.content.Context
import android.util.Log
import com.example.drawingapp.model.Drawing
import com.example.drawingapp.model.FileHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object DrawingApiService {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    const val BASE_URL = "http://10.0.2.2:8080"

    suspend fun uploadDrawing(context: Context, drawing: Drawing): Boolean = withContext(Dispatchers.IO) {
        val fileHandler = FileHandler(context)
        val imageBytes = fileHandler.loadDrawingAsByteArray(drawing.filename)

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("drawing", "drawing.json",
                json.encodeToString(drawing).toRequestBody("application/json".toMediaType()))
            .addFormDataPart("image", drawing.filename,
                imageBytes.toRequestBody("image/png".toMediaType()))
            .build()

        val request = Request.Builder()
            .url("$BASE_URL/uploadDrawing")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            response.use { it.isSuccessful }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun getSharedDrawings(): List<Drawing> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$BASE_URL/sharedDrawings")
            .get()
            .build()

        try {
            val response = client.newCall(request).execute()
            response.use {
                if (!it.isSuccessful) return@withContext emptyList()
                val jsonBody = it.body?.string() ?: return@withContext emptyList()
                json.decodeFromString<List<Drawing>>(jsonBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
