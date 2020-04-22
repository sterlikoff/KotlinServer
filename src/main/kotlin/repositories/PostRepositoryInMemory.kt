package repositories

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import models.Post

class PostRepositoryInMemory : PostRepository {

    private var nextId = 1
    private val items = mutableListOf<Post>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<Post> {
        mutex.withLock {
            return items.reversed()
        }
    }

    override suspend fun getById(id: Int): Post? {
        mutex.withLock {
            return items.find { it.id == id }
        }
    }

    override suspend fun save(item: Post): Post {

        mutex.withLock {

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

    }

    override suspend fun removeById(id: Int) {

        mutex.withLock {
            items.removeIf { it.id == id }
        }

    }

    override suspend fun likeById(id: Int) {

        mutex.withLock {

            val model = items.find { it.id == id } ?: return@withLock null
            save(model.copy(likeCount = model.likeCount.inc()))

        }

    }

    override suspend fun dislikeById(id: Int) {

        mutex.withLock {

            val model = items.find { it.id == id } ?: return@withLock null
            save(model.copy(likeCount = model.likeCount.dec()))

        }

    }

    override suspend fun rePost(id: Int) {

        mutex.withLock {

            val model = getById(id)
            model?.copy(rePostCount = model.rePostCount.inc())?.let { save(it) }

        }

    }

}
