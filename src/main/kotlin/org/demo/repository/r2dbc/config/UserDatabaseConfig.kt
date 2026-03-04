package org.demo.repository.r2dbc.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.client.SSLMode
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserDatabaseConfig {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        logger.info("User DB Connecting...")
    }

    @Bean
    fun userDatabase(connectionPool: ConnectionPool): R2dbcDatabase =
        R2dbcDatabase.connect(
            connectionFactory = connectionPool,
            databaseConfig =
                R2dbcDatabaseConfig {
                    defaultMaxAttempts = 5
                    explicitDialect = PostgreSQLDialect()
                },
        )

    @Bean
    fun connectionPool(): ConnectionPool {
        val config =
            ConnectionPoolConfiguration
                .builder(
                    PostgresqlConnectionFactory(
                        PostgresqlConnectionConfiguration
                            .builder()
                            .host("localhost")
                            .port(5432)
                            .database("demo_user")
                            .username("demo_userdb")
                            .password("demo_userdb")
                            .sslMode(SSLMode.DISABLE)
                            .build(),
                    ),
                ).maxSize(20)
                .initialSize(5)
                .build()

        return ConnectionPool(config)
    }
}
