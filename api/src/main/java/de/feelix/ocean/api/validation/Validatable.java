package de.feelix.ocean.api.validation;

/**
 * Interface for objects that can be validated.
 * Implementing classes should provide validation logic to ensure their state is valid.
 */
public interface Validatable {
    /**
     * Validates the object's state.
     *
     * @throws ValidationException if the object's state is invalid
     */
    void validate() throws ValidationException;
    
    /**
     * Checks if the object's state is valid.
     *
     * @return true if the object's state is valid, false otherwise
     */
    default boolean isValid() {
        try {
            validate();
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }
}