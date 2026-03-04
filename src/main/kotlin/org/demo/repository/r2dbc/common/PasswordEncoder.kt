package org.demo.repository.r2dbc.common

interface PasswordEncoder {
    fun encode(raw: String): String

    fun matches(
        raw: String,
        encodedTarget: String,
    ): Boolean
}
