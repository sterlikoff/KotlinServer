package models

import io.ktor.auth.Principal

data class User(
    val id: Int = 0,
    val username: String,
    val password: String
): Principal