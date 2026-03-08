package org.demo.repository.r2dbc.infra.exposed.annotation

import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.stereotype.Repository
import org.springframework.util.Assert

class ExposedR2dbcExceptionTranslationPostProcessor : AbstractBeanFactoryAwareAdvisingPostProcessor() {
    private var repositoryAnnotationType: Class<out Annotation> = Repository::class.java

    fun setRepositoryAnnotationType(repositoryAnnotationType: Class<out Annotation>) {
        Assert.notNull(repositoryAnnotationType, "'repositoryAnnotationType' must not be null")
        this.repositoryAnnotationType = repositoryAnnotationType
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        super.setBeanFactory(beanFactory)

        if (beanFactory !is ListableBeanFactory) {
            throw IllegalArgumentException(
                "Cannot use ExposedExceptionTranslator autodetection without ListableBeanFactory",
            )
        }

        this.advisor = ExposedR2dbcExceptionTranslationAdvisor(this.repositoryAnnotationType)
    }
}
