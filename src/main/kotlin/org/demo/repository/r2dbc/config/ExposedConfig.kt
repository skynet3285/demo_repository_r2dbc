package org.demo.repository.r2dbc.config

import org.demo.repository.r2dbc.infra.exposed.annotation.ExposedR2dbcExceptionTranslationPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExposedConfig {
    @Bean
    fun exposedR2dbcExceptionTranslationPostProcessor(): ExposedR2dbcExceptionTranslationPostProcessor =
        ExposedR2dbcExceptionTranslationPostProcessor()
}
