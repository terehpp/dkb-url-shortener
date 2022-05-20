package com.tpp.shortner.validation

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.MalformedURLException
import java.net.URL
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
@Constraint(validatedBy = [UrlValidator::class])
annotation class ValidUrl(
    val message: String = "{javax.validation.constraints.NotBlank.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UrlValidator() : ConstraintValidator<ValidUrl, String> {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UrlValidator::class.java)
    }

    override fun isValid(url: String?, ctx: ConstraintValidatorContext?): Boolean {
        try {
            URL(url)
        } catch (e: MalformedURLException) {
            logger.info("""Invalid url in request $url""")
            throw e;
        }
        return true
    }
}