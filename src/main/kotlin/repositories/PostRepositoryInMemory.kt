package repositories

import models.Post

class PostRepositoryInMemory : PostRepository {

    private var nextId = 1
    private val items = mutableListOf<Post>()

    override suspend fun getAll(): List<Post> {
        return items.reversed()
    }

    override suspend fun getById(id: Int): Post? {
        return items.find { it.id == id }
    }

    override suspend fun save(item: Post): Post {
        return when (val index = items.indexOfFirst { it.id == item.id }) {
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

    override suspend fun removeById(id: Int) {
        items.removeIf { it.id == id }
    }

    override suspend fun likeById(id: Int): Post? {
        TODO("Not yet implemented")
    }

    override suspend fun dislikeById(id: Int): Post? {
        TODO("Not yet implemented")
    }

}
