package models

data class PostInputDto(
    val title: String,
    val author: String,
    val lon: Double? = null,
    val lat: Double? = null,
    val videoUrl: String? = null,
    val advertUrl: String? = null
)