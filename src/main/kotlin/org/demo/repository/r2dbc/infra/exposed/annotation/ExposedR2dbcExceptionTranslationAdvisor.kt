package org.demo.repository.r2dbc.infra.exposed.annotation

import org.aopalliance.aop.Advice
import org.demo.repository.r2dbc.infra.exposed.support.ExposedR2dbcExceptionTranslationInterceptor
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractPointcutAdvisor
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut

class ExposedR2dbcExceptionTranslationAdvisor(
    repositoryAnnotationType: Class<out Annotation>,
) : AbstractPointcutAdvisor() {
    private val advice = ExposedR2dbcExceptionTranslationInterceptor()
    private val pointcut = AnnotationMatchingPointcut(repositoryAnnotationType, true)

    override fun getAdvice(): Advice = this.advice

    override fun getPointcut(): Pointcut = this.pointcut
}
