package in.edu.kristujayanti.exception;

public class BootstrapException extends RuntimeException {
    public BootstrapException(String customExceptionMessage, Throwable throwable) {
        super(customExceptionMessage, throwable);
    }

    public BootstrapException(String customExceptionMessage) {
        super(customExceptionMessage);
    }
}
