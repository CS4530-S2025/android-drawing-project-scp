package com.example.server

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

fun Route.authRoutes() {
    post("/signup") {
        val authRequest = call.receive<AuthRequest>()
        println("New signup request: ${authRequest.username}")

        call.respond(AuthResponse(token = "fake-token-${authRequest.username}"))
    }

    post("/login") {
        val authRequest = call.receive<AuthRequest>()
        println("Login attempt: ${authRequest.username}")

        if (authRequest.password == "password123") {
            call.respond(AuthResponse(token = "fake-token-${authRequest.username}"))
        } else {
            call.respondText("Invalid credentials", status = io.ktor.http.HttpStatusCode.Unauthorized)
        }
    }
}
