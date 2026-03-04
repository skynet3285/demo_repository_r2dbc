package org.demo.repository.r2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoRepositoryR2dbcApplication

fun main(args: Array<String>) {
    runApplication<DemoRepositoryR2dbcApplication>(*args)
}
