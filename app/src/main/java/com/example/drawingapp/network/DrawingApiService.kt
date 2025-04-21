package com.example.drawingapp.network

import android.util.Log
import com.example.drawingapp.model.Drawing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object DrawingApiService {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private const val BASE_URL = "http://10.0.2.2:8080"

    suspend fun uploadDrawing(drawing: Drawing): Boolean = withContext(Dispatchers.IO) {
        val jsonBody = json.encodeToString(drawing)
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/uploadDrawing")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            response.use { it.isSuccessful }
        } catch (e: Exception) {
            Log.e("Upload", "Upload failed: ${e.localizedMessage}", e)
            false
        }
    }
}
