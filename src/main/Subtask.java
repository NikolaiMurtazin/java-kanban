/**
 * Класс для представления подзадачи. Подзадача является частью одного конкретного эпика.
 * Наследуется от базового класса {@link Task}.
 */
public class Subtask extends Task {
    // Уникальный идентификатор эпика, к которому относится эта подзадача.
    private final int epicId;

    /**
     * Конструктор для создания новой подзадачи.
     * Статус подзадачи может быть задан при создании.
     * Подзадача должна быть связана с существующим эпиком через его ID.
     *
     * @param name        Название подзадачи.
     * @param description Описание подзадачи.
     * @param status      Начальный статус подзадачи.
     * @param epicId      Уникальный идентификатор родительского эпика, к которому относится эта подзадача.
     */
    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    /**
     * Получает уникальный идентификатор эпика, к которому относится эта подзадача.
     *
     * @return ID родительского эпика.
     */
    public int getEpicId() {
        return epicId;
    }

    /**
     * Возвращает строковое представление объекта подзадачи.
     * Включает информацию из базового класса {@link Task}
     * и идентификатор родительского эпика.
     *
     * @return Форматированная строка с данными о подзадаче.
     */
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}
