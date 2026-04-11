package uk.ac.uel.clinicmanagementsystem.util;

/**
 * General-purpose exception for application-level errors
 */
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
