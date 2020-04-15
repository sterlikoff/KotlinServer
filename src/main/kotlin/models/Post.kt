package models

data class Post(

    val id: Int,
    val title: String,
    val author: String,
    private val time: Int,
    var likeCount: Int = 0,
    val commentCount: Int = 0,
    val rePostCount: Int = 0,
    val lon: Double? = null,
    val lat: Double? = null,
    val videoUrl: String? = null,
    val parent: Post? = null,
    val advertUrl: String? = null

)