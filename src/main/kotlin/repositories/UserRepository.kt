package repositories

import models.User

interface UserRepository {

    suspend fun getAll(): List<User>
    suspend fun getById(id: Int): User?
    suspend fun getByIds(ids: Collection<Int>): List<User>
    suspend fun getByUsername(username: String): User?
    suspend fun save(item: User): User

}