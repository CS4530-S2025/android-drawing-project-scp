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

    val uploadDir = File("uploads")
    if (!uploadDir.exists()) {
        uploadDir.mkdirs()
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

            println("Received POST request to /uploadDrawing")


            multipart.forEachPart { part ->
                println("Part = ${part::class.simpleName}, name=${part.name}, filename=${(part as? PartData.FileItem)?.originalFileName}")

                when (part) {
                    is PartData.FormItem -> {
                        println("Received form item: ${part.name}")
                        if (part.name == "drawing") {
                            try {
                                drawing = Json.decodeFromString<Drawing>(part.value)
                                println("Parsed drawing: $drawing")
                            } catch (e: Exception) {
                                println("Failed to parse drawing JSON: ${e.localizedMessage}")
                            }
                        }
                    }

                    is PartData.FileItem -> {
                        if (part.name == "image") {
                            val fileName = part.originalFileName ?: "uploaded.png"
                            val fileBytes = part.streamProvider().readBytes()
                            File("uploads/$fileName").writeBytes(fileBytes)
                            imageFileName = fileName
                            println("Saved image to uploads/$fileName")
                        }
                    }

                    else -> {
                        println("⚠Unknown multipart part: $part")
                    }
                }

                part.dispose()
            }

            if (drawing != null && imageFileName != null) {
                drawingStorage.add(drawing!!)
                println("Drawing stored successfully.")
                call.respondText("Drawing received")
            } else {
                println("Missing data: drawing=$drawing, image=$imageFileName")
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
