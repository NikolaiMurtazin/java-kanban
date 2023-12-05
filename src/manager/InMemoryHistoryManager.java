package manager;

import interfaces.HistoryManager;
import task.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    /*
        Не совсем понимаю, правильно ли я использовал клонирование, также я поменял ArrayList на LinkedList,
        но нашел информации, как ограничить LinkedList 10 ячейками. Или этого не нужно делать, так как я уже
        ограничил размер LinkedList через if ?
    */

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
    public LinkedList<Task> getHistory() {
        return (LinkedList<Task>) taskViewHistory.clone();
    }
}
