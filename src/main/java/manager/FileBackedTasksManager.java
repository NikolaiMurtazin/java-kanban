package manager;

import exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import utils.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
        if (!(Files.exists(file.toPath()))) {
            try {
                Files.createFile(file.toPath());
                System.out.println("Файл успешно создан: " + file);
            } catch (IOException e) {
                System.err.println("Ошибка при создании файла: " + e.getMessage());
            }
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(String.valueOf(file), StandardCharsets.UTF_8)) {

            writer.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : allTasks.values()) {
                writer.write(String.format(task.toStringForFile()));
            }

            for (Epic epic : allEpics.values()) {
                writer.write(String.format(epic.toStringForFile()));
            }

            for (Subtask subtask : allSubtasks.values()) {
                writer.write(String.format(subtask.toStringForFile()));
            }

            writer.write("\n");

            for (Task task : getHistory()) {
                writer.write(task.getId() + ",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удается записать файл");
        }
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager backedManager = new FileBackedTasksManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while (fileReader.ready()) {
                line = fileReader.readLine();

                if (line.contains("id")) {
                    continue;
                }

                if (line.isEmpty()) {
                    break;
                }

                Task task = FromString.taskFromString(line);

                if (task.getType().equals(Type.EPIC)) {
                    backedManager.allEpics.put(task.getId(), (Epic) task);
                } else if (task.getType().equals(Type.SUBTASK)) {
                    backedManager.allSubtasks.put(task.getId(), (Subtask) task);
                    if (backedManager.allEpics.containsKey(((Subtask) task).getEpicId())) {
                        backedManager.allSubtasks.put(task.getId(), (Subtask) task);
                        backedManager.addTaskToPrioritizedList(task);
                        Epic epic = backedManager.allEpics.get(((Subtask) task).getEpicId());
                        epic.addSubtask((Subtask) task);
                    }
                } else {
                    backedManager.allTasks.put(task.getId(), task);
                    backedManager.addTaskToPrioritizedList(task);
                }
            }

            String lineWithHistory = null;
            while ((line = fileReader.readLine()) != null) {
                lineWithHistory = line;
            }

            for (int id : FromString.historyFromString(lineWithHistory)) {
                if (backedManager.allTasks.containsKey(id)) {
                    backedManager.historyManager.add(backedManager.allTasks.get(id));
                } else if (backedManager.allEpics.containsKey(id)) {
                    backedManager.historyManager.add(backedManager.allEpics.get(id));
                } else if (backedManager.allSubtasks.containsKey(id)) {
                    backedManager.historyManager.add(backedManager.allSubtasks.get(id));
                }
            }
            return backedManager;
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
    public Task createNewTask(Task task) {
        super.createNewTask(task);
        save();
        return task;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
        return subtask;
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

        File file = new File(System.getProperty("user.dir") + "/src/main/resources/tasks.csv");

        FileBackedTasksManager testSave = new FileBackedTasksManager(file);

        Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач", LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        Task task2 = new Task("Deutsch", "Учить слова");

        testSave.createNewTask(task1);
        testSave.createNewTask(task2);

        Epic epic3 = new Epic("Крупный проект", "Реализация крупного проекта");

        testSave.createNewEpic(epic3);

        Subtask subtask4 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic3.getId(), LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
        Subtask subtask5 = new Subtask("Подзадача 2", "Завершить разработку", epic3.getId(), LocalDateTime.of(2024, 2, 1, 16, 30, 0), 20);
        Subtask subtask6 = new Subtask("Подзадача 3", "Посмотреть видео по программированию", epic3.getId(), LocalDateTime.of(2024, 2, 1, 17, 50, 0), 10);

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

        System.out.println(testSave.getAllTask());
        System.out.println(testSave.getAllEpic());
        System.out.println(testSave.getAllSubtask());
        System.out.println(testSave.getHistory());

        System.out.println();

        FileBackedTasksManager testLoad = FileBackedTasksManager.loadFromFile(file);
        System.out.println(testLoad.getAllTask());
        System.out.println(testLoad.getAllEpic());
        System.out.println(testLoad.getAllSubtask());
        System.out.println(testLoad.getHistory());
    }
}
