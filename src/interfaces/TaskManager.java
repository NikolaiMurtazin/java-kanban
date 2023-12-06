package interfaces;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpic();

    ArrayList<Subtask> getAllSubtask();

    //удаление всех задач, эпиков и подзадач
    void deleteAllTasks();

    void deleteAllEpic();

    void deleteAllSubtask();

    // Получение по идентификатору
    Task getTaskById(Integer idTask);

    Epic getEpicById(Integer idEpic);

    Subtask getSubtaskById(Integer idSubtask);

    // Создание задач, эпиков и подзадач
    void createNewTask(Task task);

    void createNewEpic(Epic epic);

    void createNewSubtask(Subtask subtask);

    //Обновление задач, эпиков, подзадач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // удаление задачи, эпика, подзадачи по id
    void deleteTask(Integer idTask);

    void deleteEpic(Integer idEpic);

    void deleteSubtask(Integer idSubtask);

    // Получение списка всех задач определенного эпика
    ArrayList<Subtask> getSubtasksByEpic(Integer idEpic);

    // Вывод истории просмотров задач по id
    List<Task> getHistory();
}
