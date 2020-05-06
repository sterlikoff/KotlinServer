package models

data class Post(

    val id: Int,
    val title: String,
    val content: String,
    val userId: Int,
    val time: Long,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val rePostCount: Int = 0,
    val lon: Double? = null,
    val lat: Double? = null,
    val videoUrl: String? = null,
    val parentId: Int? = null,
    val advertUrl: String? = null,
    val imageId: String? = null

)