package com.spoolbear.validations.impl;

import com.spoolbear.model.enums.IPVersion;
import com.spoolbear.model.enums.ReactionTypes;
import com.spoolbear.model.response.ValidationResultResponse;
import com.spoolbear.util.RegexPatternsForValidations;
import com.spoolbear.validations.CommonValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class CommonValidationServiceImpl implements CommonValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonValidationServiceImpl.class);

    @Override
    public ValidationResultResponse validateNotNullOrEmpty(String field, String value) {
        LOGGER.debug("Validating field '{}' for not null or empty: {}", field, value);

        boolean isValid = value != null && !value.trim().isEmpty();

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field cannot be null or empty")
                .additionalInfo(isValid ? "Field has valid content" :
                        value == null ? "Field is null" : "Field is empty or contains only whitespace")
                .build();
    }

    @Override
    public ValidationResultResponse validateNotNull(String field, String value) {
        LOGGER.debug("Validating field '{}' for not null: {}", field, value);

        boolean isValid = value != null;

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field cannot be null")
                .additionalInfo(isValid ? "Field is not null" : "Field is null")
                .build();
    }

    @Override
    public ValidationResultResponse validateNotEmpty(String field, String value) {
        LOGGER.debug("Validating field '{}' for not empty: {}", field, value);

        if (value == null) {
            return ValidationResultResponse.builder()
                    .valid(false)
                    .field(field)
                    .message("Field cannot be null")
                    .additionalInfo("Field is null")
                    .build();
        }

        boolean isValid = !value.trim().isEmpty();

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field cannot be empty")
                .additionalInfo(isValid ? "Field is not empty" : "Field is empty or contains only whitespace")
                .build();
    }

    @Override
    public ValidationResultResponse validateNotBlank(String field, String value) {
        LOGGER.debug("Validating field '{}' for not blank: {}", field, value);

        boolean isValid = value != null && !value.isBlank();

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field cannot be blank")
                .additionalInfo(isValid ? "Field has non-whitespace content" :
                        value == null ? "Field is null" : "Field contains only whitespace")
                .build();
    }

    // Existing methods from previous implementation...
    @Override
    public ValidationResultResponse validateOnlyAlphabets(String field, String value) {
        LOGGER.debug("Validating field '{}' for only alphabets: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.ONLY_ALPHABETS);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field should contain only alphabetic characters")
                .additionalInfo(isValid ? "" : "Found non-alphabetic characters")
                .build();
    }

    @Override
    public ValidationResultResponse validateOnlyNumbers(String field, String value) {
        LOGGER.debug("Validating field '{}' for only numbers: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.ONLY_NUMBERS);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field should contain only numeric characters")
                .additionalInfo(isValid ? "" : "Found non-numeric characters")
                .build();
    }

    @Override
    public ValidationResultResponse validateAlphanumericWithHyphenUnderscore(String field, String value) {
        LOGGER.debug("Validating field '{}' for alphanumeric with hyphen/underscore: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.ALPHANUMERIC_WITH_HYPHEN_UNDERSCORE);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field should contain only alphanumeric characters, hyphens, or underscores")
                .additionalInfo(isValid ? "" : "Found invalid characters")
                .build();
    }

    @Override
    public ValidationResultResponse validateContainsSpecialCharacters(String field, String value) {
        LOGGER.debug("Validating field '{}' for special characters: {}", field, value);

        if (value == null || value.isBlank()) {
            return ValidationResultResponse.builder()
                    .valid(false)
                    .field(field)
                    .message("Field cannot be null or blank")
                    .additionalInfo(value == null ? "Field is null" : "Field is blank")
                    .build();
        }

        boolean containsSpecialChars = value.matches(RegexPatternsForValidations.CONTAINS_SPECIAL_CHARS);

        return ValidationResultResponse.builder()
                .valid(containsSpecialChars)
                .field(field)
                .message(containsSpecialChars ? "Field contains special characters" : "No special characters found")
                .additionalInfo(containsSpecialChars ? "Special characters detected" : "Only basic characters present")
                .build();
    }

    @Override
    public ValidationResultResponse validateNoSpecialCharacters(String field, String value) {
        LOGGER.debug("Validating field '{}' for no special characters: {}", field, value);

        if (value == null || value.isBlank()) {
            return ValidationResultResponse.builder()
                    .valid(false)
                    .field(field)
                    .message("Field cannot be null or blank")
                    .additionalInfo(value == null ? "Field is null" : "Field is blank")
                    .build();
        }

        boolean hasNoSpecialChars = value.matches(RegexPatternsForValidations.NO_SPECIAL_CHARS);

        return ValidationResultResponse.builder()
                .valid(hasNoSpecialChars)
                .field(field)
                .message(hasNoSpecialChars ? "Validation successful" : "Field contains special characters")
                .additionalInfo(hasNoSpecialChars ? "No special characters found" : "Special characters detected")
                .build();
    }

    @Override
    public ValidationResultResponse validateAlphanumeric(String field, String value) {
        LOGGER.debug("Validating field '{}' for alphanumeric: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.ALPHANUMERIC);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field should contain only alphanumeric characters")
                .additionalInfo(isValid ? "" : "Found non-alphanumeric characters")
                .build();
    }

    @Override
    public ValidationResultResponse validateMinLength(String field, String value, int minLength) {
        LOGGER.debug("Validating field '{}' for minimum length {}: {}", field, minLength, value);

        // First check if not null
        ValidationResultResponse nullCheck = validateNotNull(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.length() >= minLength;

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field length is less than required minimum")
                .additionalInfo(isValid ?
                        String.format("Length: %d (min: %d)", value.length(), minLength) :
                        String.format("Length: %d (required min: %d)", value.length(), minLength))
                .build();
    }

    @Override
    public ValidationResultResponse validateMaxLength(String field, String value, int maxLength) {
        LOGGER.debug("Validating field '{}' for maximum length {}: {}", field, maxLength, value);

        if (value == null) {
            return ValidationResultResponse.builder()
                    .valid(true) // Null is within max length
                    .field(field)
                    .message("Validation successful")
                    .additionalInfo("Null value - within length limits")
                    .build();
        }

        boolean isValid = value.length() <= maxLength;

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field length exceeds maximum allowed")
                .additionalInfo(isValid ?
                        String.format("Length: %d (max: %d)", value.length(), maxLength) :
                        String.format("Length: %d (allowed max: %d)", value.length(), maxLength))
                .build();
    }

    @Override
    public ValidationResultResponse validateIPAddress(String field, String value) {
        LOGGER.debug("Validating field '{}' for IP address: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.IP_ADDRESS_PATTERN);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Invalid IP address format")
                .additionalInfo(isValid ?
                        String.format("Valid IP address: %s", value) :
                        String.format("Invalid IP address format: %s", value))
                .build();
    }

    @Override
    public ValidationResultResponse validateIPv4Address(String field, String value) {
        LOGGER.debug("Validating field '{}' for IPv4 address: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.IPV4_PATTERN);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Invalid IPv4 address format")
                .additionalInfo(isValid ?
                        String.format("Valid IPv4 address: %s", value) :
                        String.format("Invalid IPv4 address format: %s. Expected format: xxx.xxx.xxx.xxx (0-255)", value))
                .build();
    }

    @Override
    public ValidationResultResponse validateIPv6Address(String field, String value) {
        LOGGER.debug("Validating field '{}' for IPv6 address: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.IPV6_PATTERN);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Invalid IPv6 address format")
                .additionalInfo(isValid ?
                        String.format("Valid IPv6 address: %s", value) :
                        String.format("Invalid IPv6 address format: %s. Expected format: xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx", value))
                .build();
    }

    @Override
    public ValidationResultResponse validateIPAddressWithVersionCheck(String field, String value, IPVersion version) {
        LOGGER.debug("Validating field '{}' for IP address with version {}: {}", field, version, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = false;
        String ipVersion = "";

        switch (version) {
            case IPV4:
                isValid = value.matches(RegexPatternsForValidations.IPV4_PATTERN);
                ipVersion = "IPv4";
                break;
            case IPV6:
                isValid = value.matches(RegexPatternsForValidations.IPV6_PATTERN);
                ipVersion = "IPv6";
                break;
            case ANY:
                isValid = value.matches(RegexPatternsForValidations.IP_ADDRESS_PATTERN);
                ipVersion = "IPv4 or IPv6";
                break;
        }

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : String.format("Invalid %s address format", ipVersion))
                .additionalInfo(isValid ?
                        String.format("Valid %s address: %s", ipVersion, value) :
                        String.format("Invalid %s address format: %s", ipVersion, value))
                .build();
    }

    @Override
    public ValidationResultResponse validateEmailFormat(String field, String value) {
        LOGGER.debug("Validating field '{}' for email format: {}", field, value);

        // First check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = value.matches(RegexPatternsForValidations.EMAIL_PATTERN);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Invalid email format")
                .additionalInfo(isValid ? "Valid email address" : "Email should be in format: user@domain.com")
                .build();
    }

    @Override
    public ValidationResultResponse validateBoolean(String field, String value) {
        LOGGER.debug("Validating field '{}' for boolean value: {}", field, value);

        // Check if not null or empty
        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Field must be a boolean value ('true' or 'false')")
                .additionalInfo(isValid ? "" : "Invalid boolean value detected")
                .build();
    }

    @Override
    public ValidationResultResponse validateMobileNumber(String countryCode, String number) {
        LOGGER.debug("Validating mobile number for country {}: {}", countryCode, number);

        ValidationResultResponse nullCheck = validateNotNullOrEmpty("mobileNumber", number);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        String cleaned = number.replaceAll("[\\s\\-()]", "");

        if (!cleaned.startsWith("+")) {
            cleaned = countryCode + cleaned.replaceFirst("^0+", "");
        }

        boolean isValid = cleaned.matches("^\\+[1-9]\\d{7,14}$");

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field("mobileNumber")
                .message(isValid ? "Validation successful" : "Invalid mobile number format")
                .additionalInfo(isValid
                        ? "Valid E.164 mobile number: " + cleaned
                        : "Expected valid E.164 number (e.g., +94771234567)")
                .build();
    }

    @Override
    public ValidationResultResponse validateRectionType(String field, String value) {
        LOGGER.debug("Validating field '{}' for reaction type: {}", field, value);

        ValidationResultResponse nullCheck = validateNotNullOrEmpty(field, value);
        if (!nullCheck.isValid()) {
            return nullCheck;
        }

        boolean isValid = false;
        try {
            ReactionTypes reaction = ReactionTypes.valueOf(value.toUpperCase());
            isValid = true;
        } catch (IllegalArgumentException e) {
            isValid = false;
        }

        String validTypes = Arrays.stream(ReactionTypes.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        return ValidationResultResponse.builder()
                .valid(isValid)
                .field(field)
                .message(isValid ? "Validation successful" : "Invalid reaction type")
                .additionalInfo(isValid ?
                        String.format("Valid reaction type: %s", value) :
                        String.format("Invalid reaction type: %s. Valid types are: %s", value, validTypes))
                .build();
    }
}
