package com.spoolbear.validations;

import com.spoolbear.model.enums.IPVersion;
import com.spoolbear.model.response.ValidationResultResponse;

public interface CommonValidationService {

    // IP Address validation methods
    ValidationResultResponse validateIPAddress(String field, String value);
    ValidationResultResponse validateIPv4Address(String field, String value);
    ValidationResultResponse validateIPv6Address(String field, String value);
    ValidationResultResponse validateIPAddressWithVersionCheck(String field, String value, IPVersion version);

    // Null/Empty validation methods
    ValidationResultResponse validateNotNullOrEmpty(String field, String value);
    ValidationResultResponse validateNotNull(String field, String value);
    ValidationResultResponse validateNotEmpty(String field, String value);
    ValidationResultResponse validateNotBlank(String field, String value);
    ValidationResultResponse validateBoolean(String field, String value);

    // mobile Number validation methods
    ValidationResultResponse validateMobileNumber(String countryCode, String value);

    //validate enums
    ValidationResultResponse validateRectionType(String field, String value);

    // Pattern validation methods
    ValidationResultResponse validateOnlyAlphabets(String field, String value);
    ValidationResultResponse validateOnlyNumbers(String field, String value);
    ValidationResultResponse validateAlphanumericWithHyphenUnderscore(String field, String value);
    ValidationResultResponse validateContainsSpecialCharacters(String field, String value);
    ValidationResultResponse validateNoSpecialCharacters(String field, String value);
    ValidationResultResponse validateAlphanumeric(String field, String value);
    ValidationResultResponse validateMinLength(String field, String value, int minLength);
    ValidationResultResponse validateMaxLength(String field, String value, int maxLength);
    ValidationResultResponse validateEmailFormat(String field, String value);
}
