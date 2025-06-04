import java.util.ArrayList;
import java.util.List;

/**
 * Класс для представления эпика. Эпик - это крупная задача,
 * которая может быть разбита на подзадачи.
 * Наследуется от класса {@link Task}.
 */
public class Epic extends Task {
    private final List<Integer> subtaskIds;

    /**
     * Конструктор для создания нового эпика.
     * Статус эпика будет рассчитан менеджером на основе статусов его подзадач.
     * Изначально список подзадач пуст.
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
     * Получает список идентификаторов подзадач, входящих в этот эпик.
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
     * Возвращает строковое представление эпика.
     * Включает информацию из базового класса Task и список ID подзадач.
     *
     * @return Строка с информацией об эпике.
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
