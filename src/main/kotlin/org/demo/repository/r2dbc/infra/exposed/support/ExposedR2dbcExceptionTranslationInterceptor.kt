package org.demo.repository.r2dbc.infra.exposed.support

import io.r2dbc.spi.R2dbcException
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.jetbrains.exposed.v1.r2dbc.ExposedR2dbcException
import org.slf4j.LoggerFactory
import org.springframework.r2dbc.connection.ConnectionFactoryUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ExposedR2dbcExceptionTranslationInterceptor : MethodInterceptor {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun invoke(invocation: MethodInvocation): Any? {
        val result = invocation.proceed()

        if (result is Mono<*>) {
            return result.onErrorMap { ex -> translateException(ex, invocation) }
        }

        if (result is Flux<*>) {
            return result.onErrorMap { ex -> translateException(ex, invocation) }
        }

        return try {
            result
        } catch (ex: Exception) {
            throw translateException(ex, invocation)
        }
    }

    private fun translateException(
        ex: Throwable,
        invocation: MethodInvocation,
    ): Throwable {
        if (ex is ExposedR2dbcException) {
            return ConnectionFactoryUtils.convertR2dbcException("Exposed Operation", null, ex.cause as R2dbcException)
        }

        if (ex is R2dbcException) {
            return ConnectionFactoryUtils.convertR2dbcException("R2dbcException Operation", null, ex)
        }

        return ex
    }
}
