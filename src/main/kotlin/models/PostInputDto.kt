package models

data class PostInputDto(
    val title: String,
    val lon: Double? = null,
    val lat: Double? = null,
    val videoUrl: String? = null,
    val advertUrl: String? = null,
    val imageId: String? = null
) {

    companion object {

        fun fromOut(model: PostOutDto) = PostInputDto(
            model.title,
            model.lon,
            model.lat,
            model.videoUrl,
            model.advertUrl,
            model.imageId
        )

    }

}