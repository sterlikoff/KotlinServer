package repositories

import models.Post

interface PostRepository {

    suspend fun getAll(): List<Post>
    suspend fun getById(id: Int): Post?
    suspend fun save(item: Post): Post
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun share(id: Int)

}