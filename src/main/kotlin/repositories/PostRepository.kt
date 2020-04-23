package repositories

import models.Post

interface PostRepository {

    suspend fun getAll(): List<Post>
    suspend fun getById(id: Int): Post?
    suspend fun save(item: Post): Post
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int): Post
    suspend fun dislikeById(id: Int): Post
    suspend fun share(id: Int): Post

}