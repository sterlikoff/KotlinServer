package models

data class PostOutDto(

    val id: Int,
    val title: String,
    val content: String,
    val username: String,
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

) {

    companion object {

        fun fromModel(model: Post, user: User) = PostOutDto(

            id = model.id,
            title = model.title,
            content = model.content,
            username = user.username,
            time = model.time,
            likeCount = model.likeCount,
            commentCount = model.commentCount,
            rePostCount = model.rePostCount,
            lon = model.lon,
            lat = model.lat,
            videoUrl = model.videoUrl,
            parentId = model.parentId,
            advertUrl = model.advertUrl,
            imageId = model.imageId

        )

    }

}