package org.demo.repository.r2dbc.api.v1.user

import org.demo.repository.r2dbc.api.v1.user.domain.User
import org.demo.repository.r2dbc.api.v1.user.dto.CreateUserDto
import org.demo.repository.r2dbc.api.v1.user.persistence.UserRepository
import org.demo.repository.r2dbc.api.v1.user.ro.UserRO
import org.demo.repository.r2dbc.api.v1.user.ro.toRO
import org.demo.repository.r2dbc.common.PasswordEncoder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    // ---- AOP TEST METHODS ----
    @Transactional
    suspend fun defaultPropagationTxMethod() {
        logger.debug("Calling defaultPropagationTxMethod()")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun requiresNewPropagationTxMethod() {
        logger.debug("Calling requiresNewPropagationTxMethod()")
    }

    private suspend fun privateInnerMethod() {
        logger.debug("Calling privateInnerMethod()")
    }

    // self-invocation 테스트
    suspend fun internalPlainCallerMethod() {
        logger.debug("Calling internalPlainCallerMethod()")
        defaultPropagationTxMethod()
        requiresNewPropagationTxMethod()
    }

    @Transactional
    suspend fun internalOuterDefaultPropagationTxMethod() {
        logger.debug("Calling internalOuterDefaultPropagationTxMethod()")
        defaultPropagationTxMethod()
        requiresNewPropagationTxMethod()
    }

    @Transactional
    suspend fun internalOuterDefaultPropagationTxMethod2() {
        logger.debug("Calling internalOuterDefaultPropagationTxMethod2()")
        privateInnerMethod()
        defaultPropagationTxMethod()
        requiresNewPropagationTxMethod()
    }

    // proxy 호출 테스트
    suspend fun externalPlainCallerMethod() {
        logger.debug("Calling externalPlainCallerMethod()")
        userRepository.defaultPropagationTxMethod()
        userRepository.requiresNewPropagationTxMethod()
    }

    @Transactional
    suspend fun externalOuterDefaultPropagationTxMethod() {
        logger.debug("Calling externalOuterDefaultPropagationTxMethod()")
        userRepository.defaultPropagationTxMethod()
        userRepository.requiresNewPropagationTxMethod()
    }
    // -----------------------

    @Transactional(readOnly = true)
    suspend fun findUserByUsername(username: String): UserRO? = userRepository.findByROUsername(username)

    @Transactional
    suspend fun signUp(input: CreateUserDto): UserRO {
        val newUser =
            User.create(
                nickname = input.nickname,
                username = input.username,
                rawPassword = input.password,
                passwordEncoder = passwordEncoder,
            )
        userRepository.create(newUser)

        return newUser.toRO()
    }

    @Transactional
    suspend fun unsafeSignUp(user: User) {
        userRepository.unsafeCreate(user)
    }

    @Transactional
    suspend fun updateNickname(
        userId: Long,
        newNickname: String,
    ): UserRO? {
        val user = userRepository.findByIdForUpdate(userId) ?: return null

        user.changeNickname(newNickname)

        userRepository.save(user)

        return user.toRO()
    }

    @Transactional(readOnly = true)
    suspend fun signIn(
        username: String,
        rawPassword: String,
    ): UserRO? {
        val user = userRepository.findByUsername(username) ?: return null

        try {
            user.authenticate(rawPassword, passwordEncoder)

            return user.toRO()
        } catch (e: Exception) {
            logger.debug(e.message, e)
            return null
        }
    }

    @Transactional
    suspend fun deleteAllUsers() {
        userRepository.deleteAll()
    }
}
