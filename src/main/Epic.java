import java.util.ArrayList;
import java.util.List;

/**
 * Класс для представления эпика. Эпик - это крупная задача,
 * которая может быть разбита на одну или несколько подзадач ({@link Subtask}).
 * Статус эпика зависит от статусов его подзадач и управляется {@link TaskManager}.
 * Наследуется от базового класса {@link Task}.
 */
public class Epic extends Task {
    // Список идентификаторов подзадач, входящих в этот эпик.
    private final List<Integer> subtaskIds;

    /**
     * Конструктор для создания нового эпика.
     * Изначально эпик создается без подзадач.
     * Его статус будет автоматически определен менеджером задач
     * на основе статусов связанных подзадач (изначально {@link TaskStatus#NEW}).
     *
     * @param name        Название эпика.
     * @param description Описание эпика.
     */
    public Epic(String name, String description) {
        super(name, description); // Статус по умолчанию будет NEW, менеджер его обновит
        this.subtaskIds = new ArrayList<>();
        // Статус эпика управляется TaskManager, поэтому здесь он может быть NEW
        // или будет пересчитан сразу после создания в менеджере.
    }

    /**
     * Получает список идентификаторов подзадач, которые связаны с этим эпиком.
     *
     * @return Список ID подзадач.
     */
    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds); // Возвращаем копию для защиты от внешних изменений
    }

    /**
     * Добавляет ID подзадачи в список подзадач эпика.
     * Этот метод обычно вызывается менеджером задач.
     *
     * @param subtaskId ID подзадачи для добавления.
     */
    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    /**
     * Удаляет ID подзадачи из списка подзадач эпика.
     * Этот метод обычно вызывается менеджером задач.
     *
     * @param subtaskId ID подзадачи для удаления.
     */
    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    /**
     * Очищает список ID подзадач эпика.
     * Этот метод обычно вызывается менеджером задач.
     */
    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    /**
     * Возвращает строковое представление объекта эпика.
     * Включает информацию из базового класса {@link Task}
     * и список идентификаторов связанных подзадач.
     *
     * @return Форматированная строка с данными об эпике.
     */
    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
