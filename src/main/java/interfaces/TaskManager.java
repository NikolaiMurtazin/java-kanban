package interfaces;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTask();

    ArrayList<Epic> getAllEpic();

    ArrayList<Subtask> getAllSubtask();

    Task createNewTask(Task task);

    Epic createNewEpic(Epic epic);

    Subtask createNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    Task getTaskById(Integer idTask);

    Epic getEpicById(Integer idEpic);

    Subtask getSubtaskById(Integer idSubtask);

    ArrayList<Subtask> getSubtasksByEpic(Integer idEpic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void deleteTask(Integer idTask);

    void deleteEpic(Integer idEpic);

    void deleteSubtask(Integer idSubtask);

    void deleteAllTasks();

    void deleteAllEpic();

    void deleteAllSubtask();
}
