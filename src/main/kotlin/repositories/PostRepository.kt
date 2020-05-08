package repositories

import models.Post
import models.User

interface PostRepository {

    suspend fun getAll(offset: Int, limit: Int): List<Post>
    suspend fun getById(id: Int): Post?
    suspend fun save(item: Post): Post
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int): Post
    suspend fun dislikeById(id: Int): Post
    suspend fun share(id: Int, user: User): Post

}