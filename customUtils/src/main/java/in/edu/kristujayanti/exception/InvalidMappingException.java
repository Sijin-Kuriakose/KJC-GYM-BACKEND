package in.edu.kristujayanti.exception;

public class InvalidMappingException extends RuntimeException {

    // Constructor with both a custom message and the cause (Throwable)
    public InvalidMappingException(String customExceptionMessage, Throwable throwable) {
        super(customExceptionMessage, throwable);
    }

    // Constructor with just a custom message
    public InvalidMappingException(String customExceptionMessage) {
        super(customExceptionMessage);
    }
}


