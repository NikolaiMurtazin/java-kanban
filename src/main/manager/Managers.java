package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

/**
 * Utility class providing singleton instances of application managers:
 * {@link TaskManager} and {@link HistoryManager}.
 * <p>
 * Implements a lazy-loaded singleton pattern to ensure only one instance
 * of each manager is created per application runtime.
 */
public class Managers {

    private static TaskManager defaultTaskManager;
    private static HistoryManager defaultHistoryManager;

    /**
     * Returns the singleton instance of {@link TaskManager}.
     * Initializes the manager and its associated {@link HistoryManager} if not yet created.
     *
     * @return a shared instance of {@code TaskManager}
     */
    public static TaskManager getDefault() {
        if (defaultTaskManager == null) {
            defaultHistoryManager = new InMemoryHistoryManager();
            defaultTaskManager = new InMemoryTaskManager(defaultHistoryManager);
        }
        return defaultTaskManager;
    }

    /**
     * Returns the singleton instance of {@link HistoryManager}.
     * Ensures it is synchronized with the {@link TaskManager} returned by {@link #getDefault()}.
     *
     * @return a shared instance of {@code HistoryManager}
     */
    public static HistoryManager getDefaultHistory() {
        getDefault(); // Ensures both managers are initialized
        return defaultHistoryManager;
    }

    // Optional: You can add a private constructor to prevent instantiation.
    private Managers() {
        // Utility class: prevent instantiation
    }
}
