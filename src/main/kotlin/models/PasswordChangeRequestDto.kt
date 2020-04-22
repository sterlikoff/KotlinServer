package models

data class PasswordChangeRequestDto (
    val old: String,
    val new: String
)