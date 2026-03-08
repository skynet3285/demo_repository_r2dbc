package org.demo.repository.r2dbc.api.v1.user.persistence

import kotlinx.coroutines.flow.singleOrNull
import org.demo.repository.r2dbc.api.v1.user.domain.User
import org.demo.repository.r2dbc.api.v1.user.ro.UserRO
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteAll
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Repository
import kotlin.random.Random

@Repository
class UserRepository {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun findById(userId: Long): User? =
        UserTable
            .selectAll()
            .where(UserTable.id eq userId)
            .singleOrNull()
            ?.toDomain()

    suspend fun findByIdForUpdate(userId: Long): User? =
        UserTable
            .selectAll()
            .where(UserTable.id eq userId)
            .forUpdate()
            .singleOrNull()
            ?.toDomain()

    suspend fun findROById(userId: Long): UserRO? =
        UserTable
            .selectAll()
            .where(UserTable.id eq userId)
            .singleOrNull()
            ?.toRO()

    suspend fun findByUsername(username: String): User? =
        UserTable
            .selectAll()
            .where(UserTable.username eq username)
            .singleOrNull()
            ?.toDomain()

    suspend fun findByROUsername(username: String): UserRO? =
        UserTable
            .selectAll()
            .where(UserTable.username eq username)
            .singleOrNull()
            ?.toRO()

    suspend fun create(user: User) {
        // TODO: TSID
        val id = System.currentTimeMillis() + Random.nextLong(1000)

        UserTable.insert {
            it[UserTable.id] = id
            it[nickname] = user.nickname
            it[username] = user.username
            it[password] = user.password
            it[status] = user.status.name
            it[createdAt] = user.createdAt
            it[lastAccessAt] = user.lastAccessAt
            it[role] = user.role.name
        }

        user.setUserId(id)
    }

    suspend fun unsafeCreate(user: User) {
        UserTable.insert {
            it[UserTable.id] = user.userId
            it[nickname] = user.nickname
            it[username] = user.username
            it[password] = user.password
            it[status] = user.status.name
            it[createdAt] = user.createdAt
            it[lastAccessAt] = user.lastAccessAt
            it[role] = user.role.name
        }
    }

    suspend fun save(user: User) {
        check(user.userId != -1L) {
            "Cannot update a user that has not been created yet."
        }

        val updatedRows =
            UserTable.update({ UserTable.id eq user.userId }) {
                it[nickname] = user.nickname
                it[username] = user.username
                it[password] = user.password
                it[status] = user.status.name
                it[lastAccessAt] = user.lastAccessAt
                it[role] = user.role.name
            }

        if (updatedRows == 0) {
            throw OptimisticLockingFailureException("data not found or already deleted: ${user.userId}")
        }
    }

    suspend fun delete(user: User): Boolean = UserTable.deleteWhere { UserTable.id eq user.userId } == 1

    suspend fun deleteAll() {
        UserTable.deleteAll()
    }
}
