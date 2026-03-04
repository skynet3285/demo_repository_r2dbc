package org.demo.repository.r2dbc.api.v1.user.persistence

import org.demo.repository.r2dbc.api.v1.user.domain.User
import org.demo.repository.r2dbc.api.v1.user.domain.UserRole
import org.demo.repository.r2dbc.api.v1.user.domain.UserStatus
import org.demo.repository.r2dbc.api.v1.user.ro.UserRO
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toRO(): UserRO =
    UserRO(
        userId = this[UserTable.id].value,
        nickname = this[UserTable.nickname],
        username = this[UserTable.username],
        status = this[UserTable.status],
        createdAt = this[UserTable.createdAt],
        lastAccessAt = this[UserTable.lastAccessAt],
        role = this[UserTable.role],
    )

fun ResultRow.toDomain(): User =
    User.from(
        userId = this[UserTable.id].value,
        nickname = this[UserTable.nickname],
        username = this[UserTable.username],
        password = this[UserTable.password],
        status = UserStatus.valueOf(this[UserTable.status]),
        createdAt = this[UserTable.createdAt],
        lastAccessAt = this[UserTable.lastAccessAt],
        role = UserRole.valueOf(this[UserTable.role]),
    )
