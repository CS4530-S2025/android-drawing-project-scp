package com.example.server

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    val drawingStorage = mutableListOf<Drawing>()

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
                            drawing = Json.decodeFromString<Drawing>(part.value)
                        }
                    }
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: "uploaded.png"
                        val fileBytes = part.streamProvider().readBytes()
                        File("uploads/$fileName").writeBytes(fileBytes)
                        imageFileName = fileName
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
            val file = File("uploads/$filename")
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound, "File not found")
            }
        }



        get("/sharedDrawings") {
            call.respond(drawingStorage)
        }
    }
}

@Serializable
data class Drawing(
    val filename: String,
    val name: String,
    val lastEdited: Long
)
