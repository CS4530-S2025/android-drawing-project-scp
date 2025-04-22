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

    private const val BASE_URL = "http://10.0.2.2:8080" // emulator-safe

    suspend fun uploadDrawing(drawing: Drawing): Boolean = withContext(Dispatchers.IO) {
        val jsonBody = json.encodeToString(drawing)
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/uploadDrawing")
            .post(requestBody)
            .build()

        Log.d("Upload", " Sending POST to $BASE_URL/uploadDrawing")
        Log.d("Upload", "Payload: $jsonBody")

        return@withContext try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            Log.d("Upload", " Response code: ${response.code}")
            Log.d("Upload", " Response body: $body")

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("Upload", " Upload failed: ${e.localizedMessage}", e)
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
