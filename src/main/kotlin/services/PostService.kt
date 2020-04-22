package services

import io.ktor.features.NotFoundException
import models.Post
import models.PostInputDto
import models.PostOutDto
import repositories.PostRepository

class PostService(
    private val repository: PostRepository
) {

    suspend fun getAll(): List<PostOutDto> {
        return repository.getAll().map { PostOutDto.fromModel(it) }
    }

    private suspend fun getModelById(id: Int): Post? {
        return repository.getById(id)
    }

    suspend fun getById(id: Int): PostOutDto {
        val model = getModelById(id) ?: throw NotFoundException()
        return PostOutDto.fromModel(model)
    }

    suspend fun save(id: Int, input: PostInputDto): PostOutDto {

        val model = when (id) {

            0 -> Post(
                0,
                input.title,
                input.author,
                System.currentTimeMillis(),
                0,
                0,
                0,
                input.lon,
                input.lat,
                input.videoUrl,
                null,
                input.advertUrl
            )
            else -> repository.getById(id)?.copy(
                title = input.title,
                author = input.author,
                lon = input.lon,
                lat = input.lat,
                videoUrl = input.videoUrl,
                advertUrl = input.advertUrl
            )

        } ?: throw NotFoundException()

        return PostOutDto.fromModel(model)

    }

    suspend fun share(id: Int) = (repository.rePost(id))
    suspend fun remove(id: Int) = repository.removeById(id)
    suspend fun like(id: Int) = repository.likeById(id)
    suspend fun dislike(id: Int) = repository.dislikeById(id)

}