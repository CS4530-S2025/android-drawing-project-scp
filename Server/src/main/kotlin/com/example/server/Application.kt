package com.example.server

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

    routing {
        get("/") {
            call.respondText("Hello from Ktor")
        }

        post("/uploadDrawing") {
            try {
                val drawing = call.receive<Drawing>()
                println("✅ Server received drawing: $drawing")
                call.respondText("Drawing received")
            } catch (e: Exception) {
                println("❌ Failed to parse drawing: ${e.message}")
                call.respondText("Bad request", status = io.ktor.http.HttpStatusCode.BadRequest)
            }
        }
    }
}

@Serializable
data class Drawing(
    val filename: String,
    val name: String,
    val lastEdited: Long
)
