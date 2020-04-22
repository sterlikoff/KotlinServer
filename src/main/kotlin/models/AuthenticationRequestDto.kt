package models

data class AuthenticationRequestDto(
    val username: String,
    val password: String
)