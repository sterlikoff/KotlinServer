package repositories

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import models.Post
import models.PostDto

class PostRepositoryInMemory : PostRepository {

    private var nextId = 1
    private val items = mutableListOf<Post>()
    private val mutex = Mutex()

    private fun get(id: Int): Post? {
        return items.find { it.id == id }!!
    }

    override suspend fun getAll(): List<PostDto> {

        return mutex.withLock {
            items.reversed().map {
                PostDto.fromModel(it)
            }
        }

    }

    override suspend fun getById(id: Int): PostDto? {
        return mutex.withLock {
            get(id)?.let { PostDto.fromModel(it) }
        }
    }

    override suspend fun save(item: Post): PostDto {

        return mutex.withLock {

            when (val index = items.indexOfFirst { it.id == item.id }) {
                -1 -> {
                    val copy = item.copy(id = nextId++)
                    items.add(copy)
                    PostDto.fromModel(copy)
                }
                else -> {
                    items[index] = item
                    PostDto.fromModel(item)
                }
            }

        }

    }

    override suspend fun removeById(id: Int) {

        mutex.withLock {
            items.removeIf { it.id == id }
        }

    }

    override suspend fun likeById(id: Int): PostDto? {

        return mutex.withLock {

            val model = items.find { it.id == id } ?: return@withLock null
            save(model.copy(likeCount = model.likeCount.inc()))
            PostDto.fromModel(model)

        }

    }

    override suspend fun dislikeById(id: Int): PostDto? {

        return mutex.withLock {

            val model = items.find { it.id == id } ?: return@withLock null
            save(model.copy(likeCount = model.likeCount.dec()))
            PostDto.fromModel(model)

        }

    }

    override suspend fun rePost(id: Int): PostDto? {

        val model = get(id) ?: return null

        save(model.copy(rePostCount = model.rePostCount.inc()))

        return save(
            Post(
                0,
                model.title,
                model.author,
                0,
                0,
                0,
                0,
                model.lon,
                model.lat,
                model.videoUrl,
                model.id,
                model.advertUrl
            )
        )
    }

}
