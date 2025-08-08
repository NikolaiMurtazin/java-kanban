package model;

import exception.ManagerSaveException;

/**
 * Utility class for converting tasks to and from CSV format.
 */
public class TaskCSVUtil {

    /**
     * Converts a {@link Task} (including Epic or Subtask) into a CSV-formatted string.
     *
     * @param task the task to convert
     * @return the CSV string representation
     * @throws ManagerSaveException if the task is null
     */
    public static String toCSVString(Task task) {
        if (task == null) {
            throw new ManagerSaveException("Cannot serialize null task");
        }

        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().toString(),
                escapeCsv(task.getName()),
                task.getStatus().toString(),
                escapeCsv(task.getDescription()),
                epicId
        );
    }

    /**
     * Parses a CSV string into a {@link Task}, {@link Epic}, or {@link Subtask} instance.
     *
     * @param value the CSV string
     * @return the corresponding task object
     * @throws ManagerSaveException if the input is invalid or parsing fails
     */
    public static Task fromCSVString(String value) {
        String[] fields = value.split(",", -1); // -1: include trailing empty strings

        if (fields.length < 5) {
            throw new ManagerSaveException("Invalid CSV line: not enough fields -> " + value);
        }

        try {
            int id = Integer.parseInt(fields[0]);
            TaskType type = TaskType.valueOf(fields[1]);
            String name = unescapeCsv(fields[2]);
            TaskStatus status = TaskStatus.valueOf(fields[3]);
            String description = unescapeCsv(fields[4]);

            return switch (type) {
                case TASK -> new Task(id, name, description, status);
                case EPIC -> new Epic(id, name, description, status);
                case SUBTASK -> {
                    if (fields.length < 6) {
                        throw new ManagerSaveException("Missing epicId for subtask: " + value);
                    }
                    int epicId = Integer.parseInt(fields[5]);
                    yield new Subtask(id, name, description, status, epicId);
                }
            };
        } catch (Exception e) {
            throw new ManagerSaveException("Error while parsing CSV line: " + value, e);
        }
    }

    // --- Optional: basic escaping in case someone puts commas in names or descriptions ---

    private static String escapeCsv(String text) {
        return text.replace(",", "\\,");
    }

    private static String unescapeCsv(String text) {
        return text.replace("\\,", ",");
    }
}
