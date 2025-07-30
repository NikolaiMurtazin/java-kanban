package manager;

import history.HistoryManager;
import history.InMemoryHistoryManager;

/**
 * Utility class providing singleton instances of application managers:
 * {@link TaskManager} and {@link HistoryManager}.
 * Uses the singleton pattern to guarantee one instance per application.
 */
public class Managers {

    private static TaskManager defaultTaskManager;
    private static HistoryManager defaultHistoryManager;

    /**
     * Returns the singleton instance of {@link TaskManager}.
     * Initializes with a default {@link HistoryManager} if necessary.
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
     * Ensures it matches the instance used by the default {@link TaskManager}.
     */
    public static HistoryManager getDefaultHistory() {
        getDefault(); // Ensures both managers are initialized
        return defaultHistoryManager;
    }
}

