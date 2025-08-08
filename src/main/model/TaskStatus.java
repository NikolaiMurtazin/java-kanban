package model;

/**
 * Enumeration representing the current status of a task.
 * <p>
 * Used for all task types: {@link Task}, {@link Epic}, and {@link Subtask}.
 */
public enum TaskStatus {
    /** The task has been created but not yet started. */
    NEW,

    /** The task is currently being worked on. */
    IN_PROGRESS,

    /** The task has been completed. */
    DONE
}

