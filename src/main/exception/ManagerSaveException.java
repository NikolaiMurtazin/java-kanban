package exception;

/**
 * Exception thrown when an error occurs while saving or loading tasks
 * in the file-backed task manager.
 */
public class ManagerSaveException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ManagerSaveException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause (can be retrieved later with {@link #getCause()})
     */
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
