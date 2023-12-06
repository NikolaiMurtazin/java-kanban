package manager;

import interfaces.HistoryManager;
import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // Спасибо за пояснения. Они очень помогают

    private final LinkedList<Task> taskViewHistory = new LinkedList<>();

    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (taskViewHistory.size() == MAX_HISTORY_SIZE) {
            taskViewHistory.removeFirst();
        }
        taskViewHistory.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(taskViewHistory);
    }
}
