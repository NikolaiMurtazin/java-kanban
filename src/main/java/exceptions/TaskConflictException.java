package exceptions;

public class TaskConflictException extends RuntimeException {
    public TaskConflictException(final String message) {
        super(message);
    }
}
