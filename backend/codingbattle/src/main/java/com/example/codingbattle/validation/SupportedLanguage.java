package com.example.codingbattle.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import com.example.codingbattle.util.CodeLanguageUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedLanguage.SupportedLanguageValidator.class)
public @interface SupportedLanguage {
    String message() default "Language is not supported. Supported languages are: java, python, cpp, c, javascript";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class SupportedLanguageValidator implements ConstraintValidator<SupportedLanguage, String> {
        @Override
        public void initialize(SupportedLanguage constraintAnnotation) {
            // No initialization needed
        }

        @Override
        public boolean isValid(String language, ConstraintValidatorContext context) {
            if (language == null || language.trim().isEmpty()) {
                return false;
            }
            return CodeLanguageUtils.isSupportedLanguage(language);
        }
    }
}
