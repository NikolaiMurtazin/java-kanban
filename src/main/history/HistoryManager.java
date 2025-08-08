package history;

import model.Task;

import java.util.List;

/**
 * Interface for managing the history of viewed {@link Task} instances.
 * Used to track and retrieve tasks that were accessed by the user,
 * typically for display in a "recently viewed" section.
 */
public interface HistoryManager {

    /**
     * Adds a task to the history.
     * <p>
     * If the task already exists in history, it is moved to the end (most recent).
     * If the task is {@code null}, no action is taken.
     *
     * @param task the task to add to history; can be null
     */
    void add(Task task);

    /**
     * Removes a task from the history by its ID.
     * <p>
     * If the ID does not exist in the history, the method does nothing.
     *
     * @param id the unique ID of the task to remove
     */
    void remove(int id);

    /**
     * Retrieves the list of tasks in the order they were last accessed.
     * <p>
     * The most recently accessed task appears at the end of the list.
     *
     * @return ordered list of previously viewed tasks; empty if no history
     */
    List<Task> getHistory();
}

