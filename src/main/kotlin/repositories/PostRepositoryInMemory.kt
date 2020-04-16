package repositories

import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import models.Post

class PostRepositoryInMemory : PostRepository {

    private var nextId = 1
    private val items = mutableListOf<Post>()
    private val context = newSingleThreadContext("PostRepository")

    override suspend fun getAll(): List<Post> {

        return withContext(context) {
            items.reversed()
        }

    }

    override suspend fun getById(id: Int): Post? {

        return withContext(context) {
            items.find { it.id == id }
        }

    }

    override suspend fun save(item: Post): Post {

        return withContext(context) {

            when (val index = items.indexOfFirst { it.id == item.id }) {
                -1 -> {
                    val copy = item.copy(id = nextId++)
                    items.add(copy)
                    copy
                }
                else -> {
                    items[index] = item
                    item
                }
            }

        }

    }

    override suspend fun removeById(id: Int) {

        withContext(context) {
            items.removeIf { it.id == id }
        }

    }

    override suspend fun likeById(id: Int): Post? {

        return withContext(context) {

            val model = items.find { it.id == id } ?: return@withContext null

            model.likeCount++
            save(model)

            model

        }

    }

    override suspend fun dislikeById(id: Int): Post? {

        return withContext(context) {

            val model = items.find { it.id == id } ?: return@withContext null

            model.likeCount--
            save(model)

            model

        }

    }

}
