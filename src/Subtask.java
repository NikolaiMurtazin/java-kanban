
/**
 * Класс для представления подзадачи. Подзадача является частью эпика.
 * Наследуется от класса {@link Task}.
 */
public class Subtask extends Task {
    private final int epicId;

    /**
     * Конструктор для создания новой подзадачи.
     *
     * @param name        Название подзадачи.
     * @param description Описание подзадачи.
     * @param status      Статус подзадачи.
     * @param epicId      Уникальный идентификатор эпика, к которому относится эта подзадача.
     */
    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    /**
     * Получает ID эпика, к которому относится эта подзадача.
     *
     * @return ID родительского эпика.
     */
    public int getEpicId() {
        return epicId;
    }

    /**
     * Возвращает строковое представление подзадачи.
     * Включает информацию из базового класса Task и ID родительского эпика.
     *
     * @return Строка с информацией о подзадаче.
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
