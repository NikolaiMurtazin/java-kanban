import java.util.Objects;

/**
 * Базовый класс для представления любой сущности в трекере задач:
 * обычной задачи, эпика или подзадачи.
 * Содержит общие атрибуты, такие как название, описание,
 * уникальный идентификатор и статус выполнения.
 */
public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;

    /**
     * Конструктор для создания новой задачи с начальным статусом {@link TaskStatus#NEW}.
     * Уникальный идентификатор (ID) задачи должен быть назначен менеджером задач
     * после её создания.
     *
     * @param name        Название, кратко описывающее суть задачи.
     * @param description Детальное описание задачи.
     */
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW; // По умолчанию новый статус
    }

    /**
     * Конструктор для создания задачи с указанным статусом.
     * Используется, например, при обновлении существующей задачи,
     * когда её статус уже известен и определен.
     * ID задачи должен быть установлен менеджером задач или уже существовать
     * для обновляемой задачи.
     *
     * @param name        Название задачи.
     * @param description Описание задачи.
     * @param status      Текущий статус задачи.
     */
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /**
     * Возвращает название задачи.
     *
     * @return Название задачи в виде строки.
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название задачи.
     *
     * @param name Новое название задачи.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает описание задачи.
     *
     * @return Описание задачи в виде строки.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание задачи.
     *
     * @param description Новое описание задачи.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает уникальный идентификатор задачи.
     *
     * @return ID задачи.
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор задачи.
     * Этот метод должен использоваться только менеджером задач при создании.
     *
     * @param id Новый ID задачи.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает текущий статус задачи.
     *
     * @return Статус задачи в виде перечисления {@link TaskStatus}.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Устанавливает новый статус для задачи.
     *
     * @param status Новый статус задачи.
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Сравнивает текущий объект задачи с другим объектом.
     * Две задачи считаются равными, если их уникальные идентификаторы (ID) совпадают.
     * Это согласуется с требованием, что задачи с одинаковым ID для менеджера
     * должны быть одной и той же задачей, независимо от их конкретного типа (Task, Epic, Subtask).
     *
     * @param o Объект, с которым производится сравнение.
     * @return {@code true}, если объекты равны по ID, {@code false} в противном случае.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Используем instanceof вместо getClass() != o.getClass()
        // Это позволяет объектам разных классов, но наследующихся от Task,
        // быть равными, если их ID совпадают.
        if (o == null || !(o instanceof Task)) return false;

        Task task = (Task) o; // Приведение типа безопасно после проверки instanceof
        return id == task.id; // Сравнение только по ID
    }

    /**
     * Генерирует хэш-код для объекта задачи.
     * Хэш-код основан исключительно на уникальном идентификаторе задачи (ID),
     * что обеспечивает согласованность с методом {@link #equals(Object)}.
     *
     * @return Целое число - хэш-код объекта.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Возвращает строковое представление объекта задачи.
     * Включает ID, название, описание и статус задачи для удобного вывода.
     *
     * @return Форматированная строка с данными о задаче.
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
