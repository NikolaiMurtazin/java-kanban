import java.util.List;

/**
 * Interface for managing the history of viewed tasks.
 */
public interface HistoryManager {
    /**
     * Adds a task to the view history.
     *
     * @param task the task to add (can be null)
     */
    void add(Task task);

    /**
     * Removes a task from the history by its ID.
     *
     * @param id the task ID
     */
    void remove(int id);

    /**
     * Returns the history of viewed tasks in access order.
     *
     * @return list of viewed tasks
     */
    List<Task> getHistory();
}

