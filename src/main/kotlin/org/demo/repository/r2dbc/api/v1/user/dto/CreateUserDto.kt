package org.demo.repository.r2dbc.api.v1.user.dto

data class CreateUserDto(
    val username: String,
    val password: String,
    val nickname: String,
)
