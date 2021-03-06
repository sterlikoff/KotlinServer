package repositories

import io.ktor.features.NotFoundException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import models.Post
import models.User

class PostRepositoryInMemory : PostRepository {

    private var nextId = 1
    private val items = mutableListOf<Post>()
    private val mutex = Mutex()

    override suspend fun getAll(offset: Int, limit: Int): List<Post> {
        mutex.withLock {
            return items.reversed().subList(offset, offset + limit)
        }
    }

    override suspend fun getById(id: Int): Post? {
        mutex.withLock {
            return getByIdInternal(id)
        }
    }

    override suspend fun save(item: Post): Post {
        mutex.withLock {
            return saveInternal(item)
        }
    }

    override suspend fun removeById(id: Int) {
        mutex.withLock {
            items.removeIf { it.id == id }
        }
    }

    override suspend fun likeById(id: Int): Post {
        mutex.withLock {
            val model = getByIdInternal(id)
            return saveInternal(model.copy(likeCount = model.likeCount.inc()))
        }
    }

    override suspend fun dislikeById(id: Int): Post {
        mutex.withLock {
            val model = getByIdInternal(id)
            return saveInternal(model.copy(likeCount = model.likeCount.dec()))
        }
    }

    override suspend fun share(id: Int, user: User): Post {

        mutex.withLock {

            val model = getByIdInternal(id)
            saveInternal(model.copy(rePostCount = model.rePostCount.inc()))

            return saveInternal(
                Post(
                    0,
                    model.title,
                    model.content,
                    user.id,
                    0,
                    0,
                    0,
                    0,
                    model.lon,
                    model.lat,
                    model.videoUrl,
                    model.id,
                    model.advertUrl,
                    model.imageId
                )
            )

        }
    }

    private fun saveInternal(item: Post): Post =
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

    private fun getByIdInternal(id: Int): Post {
        return items.find { it.id == id } ?: throw NotFoundException()
    }

}