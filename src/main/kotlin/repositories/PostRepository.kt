package repositories

import models.Post
import models.PostDto

interface PostRepository {

    suspend fun getAll(): List<PostDto>
    suspend fun getById(id: Int): PostDto?
    suspend fun save(item: Post): PostDto
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int): PostDto?
    suspend fun dislikeById(id: Int): PostDto?
    suspend fun rePost(id: Int): PostDto?

}
