package manager;

import exception.ManagerSaveException;
import history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static model.TaskCSVUtil.toCSVString;

public class FileBackedTaskManager extends InMemoryTaskManager{
    private final Path path;
    /**
     * Creates an in-memory task manager with the given history manager.
     *
     * @param historyManager the history manager to use
     */
    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    protected void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task :getAllTasks()) {
                writer.write(toCSVString(task));
                writer.newLine();
            }

            for (Epic epic :getAllEpics()) {
                writer.write(toCSVString(epic));
                writer.newLine();
            }

            for (Subtask subtask :getAllSubtasks()) {
                writer.write(toCSVString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + path, e);
        }
    }


}
