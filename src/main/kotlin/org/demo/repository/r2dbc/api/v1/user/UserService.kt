package org.demo.repository.r2dbc.api.v1.user

import org.demo.repository.r2dbc.api.v1.user.domain.User
import org.demo.repository.r2dbc.api.v1.user.dto.CreateUserDto
import org.demo.repository.r2dbc.api.v1.user.persistence.UserRepository
import org.demo.repository.r2dbc.api.v1.user.ro.UserRO
import org.demo.repository.r2dbc.api.v1.user.ro.toRO
import org.demo.repository.r2dbc.common.PasswordEncoder
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun findUserByUsername(username: String): UserRO? =
        suspendTransaction(readOnly = true) {
            userRepository.findByROUsername(username)
        }

    suspend fun signUp(input: CreateUserDto): UserRO =
        suspendTransaction {
            val newUser =
                User.create(
                    nickname = input.nickname,
                    username = input.username,
                    rawPassword = input.password,
                    passwordEncoder = passwordEncoder,
                )
            userRepository.create(newUser)

            return@suspendTransaction newUser.toRO()
        }

    suspend fun unsafeSignUp(user: User) =
        suspendTransaction {
            userRepository.unsafeCreate(user)
        }

    suspend fun updateNickname(
        userId: Long,
        newNickname: String,
    ): UserRO? =
        suspendTransaction {
            val user = userRepository.findByIdForUpdate(userId) ?: return@suspendTransaction null

            user.changeNickname(newNickname)

            userRepository.save(user)

            return@suspendTransaction user.toRO()
        }

    suspend fun signIn(
        username: String,
        rawPassword: String,
    ): UserRO? =
        suspendTransaction(readOnly = true) {
            val user = userRepository.findByUsername(username) ?: return@suspendTransaction null

            try {
                user.authenticate(rawPassword, passwordEncoder)

                return@suspendTransaction user.toRO()
            } catch (e: Exception) {
                logger.debug(e.message, e)
                return@suspendTransaction null
            }
        }

    suspend fun deleteAllUsers() =
        suspendTransaction {
            userRepository.deleteAll()
        }
}
