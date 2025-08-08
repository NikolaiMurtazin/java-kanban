package manager;

import exception.ManagerSaveException;
import history.HistoryManager;
import history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static model.TaskCSVUtil.fromCSVString;
import static model.TaskCSVUtil.toCSVString;

/**
 * Task manager that extends {@link InMemoryTaskManager} and adds file persistence.
 * It automatically saves the task state to a CSV file on each mutation and can restore state from it.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    /**
     * Creates a new file-backed task manager with a specific file path and history manager.
     *
     * @param historyManager the history manager to use for task history
     * @param path           the file path to save and load tasks
     */
    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    /**
     * Saves the current state of all tasks, epics, and subtasks to the specified file in CSV format.
     * Throws {@link ManagerSaveException} if the file cannot be written.
     */
    protected void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toCSVString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toCSVString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toCSVString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + path, e);
        }
    }

    /**
     * Loads tasks from a file and recreates the task manager state, including epics and subtasks.
     * The file should be in the CSV format used by {@link #save()}.
     *
     * @param path the path to the file to load
     * @return a new {@code FileBackedTaskManager} instance containing the loaded tasks
     * @throws ManagerSaveException if loading from the file fails
     */
    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), path);

        try {
            List<String> lines = Files.readAllLines(path);
            int maxId = 0;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                Task task = fromCSVString(line);

                maxId = Math.max(maxId, task.getId());

                if (task instanceof Subtask subtask) {
                    manager.getSubtasks().put(subtask.getId(), subtask);
                    Epic epic = manager.getEpics().get(subtask.getEpicId());
                    if (epic != null) {
                        epic.addSubtaskId(subtask.getId());
                    }
                } else if (task instanceof Epic) {
                    manager.getEpics().put(task.getId(), (Epic) task);
                } else {
                    manager.getTasks().put(task.getId(), task);
                }
            }

            manager.setNextId(maxId + 1);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + path, e);
        }

        return manager;
    }

    // --- Overridden mutation methods with file persistence ---

    @Override
    public Task createTask(Task task) {
        Task result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic result = super.createEpic(epic);
        save();
        return result;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask result = super.createSubtask(subtask);
        save();
        return result;
    }

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

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
}
