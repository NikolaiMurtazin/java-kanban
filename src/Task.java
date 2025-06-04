import java.util.Objects;

/**
 * Базовый класс для представления задачи.
 * Содержит основные атрибуты: название, описание, уникальный идентификатор и статус.
 */
public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;

    /**
     * Конструктор для создания новой задачи.
     * Статус по умолчанию устанавливается в NEW.
     * ID задачи должен быть установлен менеджером задач.
     *
     * @param name        Название задачи.
     * @param description Описание задачи.
     */
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW; // По умолчанию новый статус
    }

    /**
     * Конструктор для создания задачи с указанным статусом.
     * Используется при обновлении задачи, когда статус уже известен.
     * ID задачи должен быть установлен менеджером задач или уже существовать.
     *
     * @param name        Название задачи.
     * @param description Описание задачи.
     * @param status      Статус задачи.
     */
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    // Сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Сравнивает эту задачу с указанным объектом.
     * Результат true, если и только если аргумент не null и является объектом Task,
     * который имеет тот же id, что и эта задача.
     *
     * @param o объект для сравнения.
     * @return true, если объекты равны, иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false; // Строгое сравнение классов для простоты
        // В более общем случае можно использовать instanceof
        // если бы логика сравнения должна была работать
        // для Task, Epic, Subtask одинаково только по ID.
        // Но т.к. у нас отдельные HashMap для каждого типа,
        // это не критично, и сравнение по getClass() более точное
        // для определения типа объекта.
        // Для данного ТЗ, где ID уникален для ВСЕХ типов задач,
        // и "две задачи с одинаковым id должны выглядеть для менеджера как одна и та же",
        // сравнение только по ID было бы более правильным,
        // если бы мы не знали, что они будут храниться в разных коллекциях.
        // Оставим сравнение по ID для hashCode и equals,
        // как рекомендовано в подсказке "Эти методы нежелательно
        // переопределять в наследниках".
        Task task = (Task) o;
        return id == task.id;
    }

    /**
     * Возвращает хэш-код для этой задачи.
     * Хэш-код основан на идентификаторе задачи.
     *
     * @return хэш-код.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Возвращает строковое представление задачи.
     *
     * @return Строка с информацией о задаче.
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
