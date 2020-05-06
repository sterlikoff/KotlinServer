package services

import exceptions.AccessDeniedException
import io.ktor.features.NotFoundException
import models.Post
import models.PostInputDto
import models.PostOutDto
import models.User
import repositories.PostRepository

class PostService(
    private val repository: PostRepository
) {

    suspend fun getAll(): List<Post> = repository.getAll()

    private suspend fun getModelById(id: Int): Post {
        return repository.getById(id) ?: throw NotFoundException()
    }

    suspend fun getById(id: Int): Post = getModelById(id)

    private fun checkAccess(post: Post, user: User) {
        if (post.userId != user.id) throw AccessDeniedException("Access denied")
    }

    suspend fun save(id: Int, input: PostInputDto, user: User): PostOutDto {

        val model = when (id) {

            0 -> Post(
                0,
                input.title,
                input.content,
                user.id,
                System.currentTimeMillis(),
                0,
                0,
                0,
                input.lon,
                input.lat,
                input.videoUrl,
                null,
                input.advertUrl,
                input.imageId
            )
            else -> {

                getModelById(id).copy(
                    title = input.title,
                    lon = input.lon,
                    lat = input.lat,
                    videoUrl = input.videoUrl,
                    advertUrl = input.advertUrl,
                    imageId = input.imageId
                )

            }

        }

        if (id != 0) checkAccess(model, user)
        return PostOutDto.fromModel(repository.save(model), user)

    }

    suspend fun remove(id: Int, user: User) {
        checkAccess(getModelById(id), user)
        repository.removeById(id)
    }

    suspend fun share(id: Int) = repository.share(id)
    suspend fun like(id: Int) = repository.likeById(id)
    suspend fun dislike(id: Int) = repository.dislikeById(id)

}