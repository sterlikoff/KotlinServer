package repositories

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import models.User

class UserRepositoryInMemory : UserRepository {

    private var nextId = 1
    private val items = mutableListOf<User>()
    private val mutex = Mutex()

    override suspend fun getAll(): List<User> {
        mutex.withLock {
            return items.toList()
        }
    }

    override suspend fun getById(id: Int): User? {
        mutex.withLock {
            return items.find { it.id == id }
        }
    }

    override suspend fun getByIds(ids: Collection<Int>): List<User> {
        mutex.withLock {
            return items.filter { ids.contains(it.id) }
        }
    }

    override suspend fun getByUsername(username: String): User? {
        mutex.withLock {
            return items.find { it.username == username }
        }
    }

    override suspend fun save(item: User): User {

        return mutex.withLock {

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

}