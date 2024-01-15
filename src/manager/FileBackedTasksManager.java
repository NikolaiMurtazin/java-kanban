package manager;
import exeptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import utils.Status;
import utils.Type;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static utils.Type.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    /*
        Будут ли дополнительные рекомендации по стилю кода? Может где то методы отсортировать так,
        чтобы читаемость повысить? А то у меня такое чуство, что столько всего написано, и читаемость стала хуже.
        Может где добавить доп проверки через if else на поиск null и тд? Это я спрашиваю за все классы.
    */

    private static final String HOME = System.getProperty("user.home");

    private static final String PATH_FILE = "dev/java-kanban/resources/";

    private static final String NAME_FILE = "data.csv";

    private final Path path = Paths.get(HOME, PATH_FILE, NAME_FILE);

    public FileBackedTasksManager() {
        if (!(Files.exists(path))) {
            try {
                Files.createFile(path);
                System.out.println("Файл успешно создан: data.csv");
            } catch (IOException e) {
                System.err.println("Ошибка при создании файла: " + e.getMessage());
            }
        }
        loadFromFile(path);
    }

    void save() {
        try (FileWriter writer = new FileWriter(String.valueOf(path), StandardCharsets.UTF_8)) {

            writer.write("id,type,name,status,description,epic");
            HashMap<Integer, String> allTasks = new HashMap<>();

            HashMap<Integer, Task> tasks = super.getAllTasks();
            for (Integer id : tasks.keySet()) {
                allTasks.put(id, tasks.get(id).toStringFromFile());
            }

            HashMap<Integer, Epic> epics = super.getAllEpics();
            for (Integer id : epics.keySet()) {
                allTasks.put(id, epics.get(id).toStringFromFile());
            }

            HashMap<Integer, Subtask> subtasks = super.getAllSubtasks();
            for (Integer id : subtasks.keySet()) {
                allTasks.put(id, subtasks.get(id).toStringFromFile());
            }

            writer.write("\n");

            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }

            writer.write("\n");

            for (Task task : getHistory()) {
                writer.write(task.getId() + ",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удается записать файл");
        }
    }

    static Task fromString(String value) {
        String[] elements = value.split(",");

        Integer id = Integer.valueOf(elements[0]);
        Type type = Type.valueOf(elements[1]);
        String title = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];
        Integer epicId = null;

        final int epicIdInElements = 6;
        if (epicIdInElements == elements.length) {
            epicId = Integer.valueOf(elements[5]);
        }
        if (type == TASK) {
            return new Task(id, type, title, description, status);
        } else if (type == EPIC) {
            return new Epic(id, type, title, description, status);
        }  else {
            return new Subtask(id, type, title, description, status, epicId);
        }
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyId = new ArrayList<>();
        if(value != null) {
            String[] idString = value.split(",");
            for (String id : idString) {
                historyId.add(Integer.valueOf(id));
            }
        }
        return historyId;
    }

    void loadFromFile(Path path) { // В ТЗ указано, что его нужна сделать статическим, но у меня не получается
        try(BufferedReader fileReader = new BufferedReader(new FileReader(String.valueOf((path))))) {
            String line;
            while (fileReader.ready()) {
                line = fileReader.readLine();

                if (line.contains("id")) {
                    continue;
                }

                if (line.isEmpty()) {
                    break;
                }

                Task task = fromString(line);

                if (task.getType().equals(EPIC)) {
                    super.createNewEpic((Epic) task); // Что это за обозначение в параметрах метода?
                } else if (task.getType().equals(SUBTASK)) {
                    super.createNewSubtask((Subtask) task);
                } else {
                    super.createNewTask(task);
                }
            }

            String lineWithHistory = null;
            while ((line = fileReader.readLine()) != null)
            {
                lineWithHistory = line;
            }

            for (int id : historyFromString(lineWithHistory)) {
                if (allTasks.containsKey(id)) {
                    historyManager.add(allTasks.get(id));
                } else if (allEpics.containsKey(id)) {
                    historyManager.add(allEpics.get(id));
                } else if (allSubtasks.containsKey(id)) {
                    historyManager.add(allSubtasks.get(id));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удается прочитать файл");
        }
    }

    //удаление всех задач, эпиков и подзадач
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    // Получение по идентификатору и занесение id в историю просмотров
    @Override
    public Task getTaskById(Integer idTask) {
        Task task = super.getTaskById(idTask);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(Integer idEpic) {
        Epic epic = super.getEpicById(idEpic);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer idSubtask) {
        Subtask subtask = super.getSubtaskById(idSubtask);
        save();
        return subtask;
    }

    // Создание задач, эпиков и подзадач
    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }

    //Обновление задач, эпиков, подзадач
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();

    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    // удаление задачи, эпика, подзадачи по id
    @Override
    public void deleteTask(Integer idTask) {
        super.deleteTask(idTask);
        save();
    }

    @Override
    public void deleteEpic(Integer idEpic) {
        super.deleteEpic(idEpic);
        save();
    }

    @Override
    public void deleteSubtask(Integer idSubtask) {
        super.deleteSubtask(idSubtask);
        save();
    }

    public static void main(String[] args) {

        FileBackedTasksManager testSave = new FileBackedTasksManager();

        Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач");
        Task task2 = new Task("Deutsch", "Учить слова");

        testSave.createNewTask(task1);
        testSave.createNewTask(task2);

        Epic epic3 = new Epic("Крупный проект", "Реализация крупного проекта");

        testSave.createNewEpic(epic3);

        Subtask subtask4 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic3.getId());
        Subtask subtask5 = new Subtask("Подзадача 2", "Завершить разработку", epic3.getId());
        Subtask subtask6 = new Subtask("Подзадача 3", "Посмотреть видео по программированию", epic3.getId());

        testSave.createNewSubtask(subtask4);
        testSave.createNewSubtask(subtask5);
        testSave.createNewSubtask(subtask6);

        Epic epic7 = new Epic("Deutsch", "Сделать домашку");

        testSave.createNewEpic(epic7);

        testSave.getTaskById(1);
        testSave.getTaskById(2);
        testSave.getEpicById(3);
        testSave.getSubtaskById(4);
        testSave.getEpicById(7);

        FileBackedTasksManager testLoad = new FileBackedTasksManager();
        System.out.println(testLoad.getAllTask());
        System.out.println(testLoad.getAllEpic());
        System.out.println(testLoad.getAllSubtask());
        System.out.println(testLoad.getHistory());

    }
}
