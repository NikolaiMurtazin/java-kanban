package model;

/**
 * Enumeration of task types used in the task manager.
 * <p>
 * These types are used to distinguish between:
 * <ul>
 *     <li>{@link #TASK} — a regular task</li>
 *     <li>{@link #EPIC} — a large task composed of multiple subtasks</li>
 *     <li>{@link #SUBTASK} — a task that belongs to an epic</li>
 * </ul>
 */
public enum TaskType {
    /** A simple, standalone task. */
    TASK,

    /** A large task consisting of multiple subtasks. */
    EPIC,

    /** A smaller task that is part of an epic. */
    SUBTASK
}
