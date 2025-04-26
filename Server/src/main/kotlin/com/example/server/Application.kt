package com.example.server

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

fun main() {
    println("🔥 Server Starting on http://0.0.0.0:8080 🔥")
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    val userDatabase = mutableMapOf<String, String>()
    val userDatabaseFile = File("userDatabase.json")
    val drawingStorage = mutableListOf<Drawing>()

    // Load users when server starts
    loadUserDatabase(userDatabase, userDatabaseFile)

    val uploadDir = File("uploads")
    if (!uploadDir.exists()) {
        uploadDir.mkdirs()
    }

    routing {
        get("/") {
            call.respondText("Hello from Ktor")
        }

        post("/uploadDrawing") {
            val multipart = call.receiveMultipart()
            var drawing: Drawing? = null
            var imageFileName: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "drawing") {
                            try {
                                drawing = Json.decodeFromString<Drawing>(part.value)
                            } catch (e: Exception) {
                                println("Failed to parse drawing JSON: ${e.localizedMessage}")
                            }
                        }
                    }

                    is PartData.FileItem -> {
                        if (part.name == "image") {
                            val fileName = part.originalFileName ?: "uploaded.png"
                            val fileBytes = part.streamProvider().readBytes()
                            File(uploadDir, fileName).writeBytes(fileBytes)
                            imageFileName = fileName
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (drawing != null && imageFileName != null) {
                drawingStorage.add(drawing!!)
                call.respondText("Drawing received")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Missing data")
            }
        }

        get("/drawingImage/{filename}") {
            val filename = call.parameters["filename"]
            val file = File(uploadDir, filename ?: "")
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound, "File not found")
            }
        }

        get("/sharedDrawings") {
            call.respond(drawingStorage)
        }

        post("/signup") {
            val authRequest = call.receive<AuthRequest>()
            if (userDatabase.containsKey(authRequest.username)) {
                call.respond(HttpStatusCode.Conflict, SignupResult(false, "User already exists"))
            } else {
                userDatabase[authRequest.username] = authRequest.password
                saveUserDatabase(userDatabase, userDatabaseFile)
                call.respond(SignupResult(true, "Signup successful"))
            }
        }

        post("/login") {
            val authRequest = call.receive<AuthRequest>()
            val savedPassword = userDatabase[authRequest.username]
            if (savedPassword == authRequest.password) {
                call.respond(AuthResponse(token = "fake-token-${authRequest.username}"))
            } else {
                call.respondText("Invalid credentials", status = HttpStatusCode.Unauthorized)
            }
        }
    }
}

// Helper functions

private fun saveUserDatabase(userDatabase: Map<String, String>, file: File) {
    val jsonContent = Json.encodeToString(MapSerializer(String.serializer(), String.serializer()), userDatabase)
    file.writeText(jsonContent)
    println("Saved user database to file.")
}


private fun loadUserDatabase(userDatabase: MutableMap<String, String>, file: File) {
    if (file.exists()) {
        val content = file.readText()
        val restored = Json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), content)
        userDatabase.clear()
        userDatabase.putAll(restored)
        println("Loaded user database from file.")
    } else {
        println("No user database file found, starting fresh.")
    }
}

// Models:

@Serializable
data class Drawing(val filename: String, val name: String, val lastEdited: Long)

@Serializable
data class AuthRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class SignupResult(val success: Boolean, val message: String)
