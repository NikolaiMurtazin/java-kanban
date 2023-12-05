package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskViewHistory = new ArrayList<>(10);
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if(taskViewHistory.size() == MAX_HISTORY_SIZE) {
            taskViewHistory.remove(0);
            taskViewHistory.add(task);
        } else {
            taskViewHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskViewHistory;
    }
}
