package com.ctang.zephyrsanctum.validators;

import java.util.Map;
import org.springframework.validation.Errors;

/**
 * Interface for validators that handle partial updates with field maps
 */
public interface FieldUpdateValidator {
    /**
     * Validates specific fields in a partial update operation
     * 
     * @param fields The map of field names to values to validate
     * @param errors The errors object to report validation failures
     */
    void validateFields(Map<String, Object> fields, Errors errors);
    
    /**
     * Determines if this validator supports the given field name
     * 
     * @param fieldName The name of the field to check
     * @return true if this validator supports validating the given field
     */
    boolean supportsField(String fieldName);
}