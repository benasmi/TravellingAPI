package com.travel.travelapi.validation

import org.passay.*
import java.util.*
import java.util.stream.Collectors
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext


class PasswordConstraintValidator : ConstraintValidator<ValidPassword?, String?> {
    override fun initialize(arg0: ValidPassword?) {}
    override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
        val validator = PasswordValidator(Arrays.asList( // at least 8 characters
                LengthRule(8, 30),  // at least one upper-case character
                CharacterRule(EnglishCharacterData.UpperCase, 1),  
                CharacterRule(EnglishCharacterData.LowerCase, 1),
                CharacterRule(EnglishCharacterData.Digit, 1),
                WhitespaceRule()
        ))
        val result: RuleResult = validator.validate(PasswordData(password))
        if (result.isValid()) {
            return true
        }
        val messages: List<String> = validator.getMessages(result)
        val messageTemplate = messages.stream()
                .collect(Collectors.joining(","))
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation()
        return false
    }
}