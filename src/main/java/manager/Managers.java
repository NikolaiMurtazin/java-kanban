package manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import server.KVTaskClient;


public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(String url) {
        return new HttpTaskManager(url, new KVTaskClient(url));
    }
}
