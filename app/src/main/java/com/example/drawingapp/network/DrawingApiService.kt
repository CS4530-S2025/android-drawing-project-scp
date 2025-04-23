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
    private const val TAG = "Upload"

    suspend fun uploadDrawing(context: Context, drawing: Drawing): Boolean = withContext(Dispatchers.IO) {
        try {
            val imageBytes = FileHandler(context).loadDrawingAsByteArray(drawing.filename)

            if (imageBytes.isEmpty()) {
                Log.e(TAG, "❌ Image byte array is empty — file may not exist!")
                return@withContext false
            }

            val drawingJson = json.encodeToString(drawing)

            Log.d("Upload", "📤 Sending drawing JSON = ${Json.encodeToString(drawing)}")

            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(
                    name = "drawing",
                    value = Json.encodeToString(drawing)
                )
                .addFormDataPart(
                    name = "image",
                    filename = drawing.filename,
                    body = imageBytes.toRequestBody("image/png".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/uploadDrawing")
                .post(requestBody)
                .build()

            Log.d(TAG, "🚀 Sending POST to $BASE_URL/uploadDrawing")
            Log.d(TAG, "📄 JSON Body: $drawingJson")
            Log.d(TAG, "📦 Image size: ${imageBytes.size} bytes")

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            Log.d(TAG, "✅ Response code: ${response.code}")
            Log.d(TAG, "✅ Response body: $body")

            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "❌ Upload failed: ${e.localizedMessage}", e)
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
                if (!it.isSuccessful) {
                    Log.e(TAG, "❌ Failed to get shared drawings. HTTP ${it.code}")
                    return@withContext emptyList()
                }

                val jsonBody = it.body?.string() ?: return@withContext emptyList()
                Log.d(TAG, "📥 Received shared drawings: $jsonBody")

                json.decodeFromString<List<Drawing>>(jsonBody)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to fetch shared drawings: ${e.localizedMessage}", e)
            emptyList()
        }
    }
}