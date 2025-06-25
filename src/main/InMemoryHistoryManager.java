import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса {@link HistoryManager}, которая хранит историю
 * просмотренных задач в оперативной памяти с использованием {@link ArrayList}.
 * Текущая реализация допускает дублирование задач в истории
 * и ограничивает её размер до {@code MAX_HISTORY_SIZE}.
 */
public class InMemoryHistoryManager implements HistoryManager {

    // Максимальное количество задач, которое может храниться в истории.
    private static final int MAX_HISTORY_SIZE = 10;
    // Список для хранения просмотренных задач.
    private final List<Task> history;

    /**
     * Конструктор по умолчанию для {@code InMemoryHistoryManager}.
     * Инициализирует пустой список для хранения истории.
     */
    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * Если размер истории достигает {@code MAX_HISTORY_SIZE},
     * самая старая задача (первый элемент) удаляется перед добавлением новой.
     * Дублирование задач в истории разрешено в данной реализации.
     * Если переданная задача {@code null}, она не будет добавлена.
     */
    @Override
    public void add(Task task) {
        if (task == null) {
            return; // Не добавляем null в историю
        }
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst(); // Удаляем самый старый элемент (первый в списке)
        }
        history.add(task); // Добавляем новую задачу в конец списка
    }

    /**
     * {@inheritDoc}
     * Возвращает копию списка текущей истории просмотров,
     * чтобы предотвратить её внешнее изменение.
     * Важно: копирование происходит в новый {@link ArrayList},
     * но объекты {@link Task} внутри списка остаются теми же ссылками.
     */
    @Override
    public List<Task> getHistory() {
        // Возвращаем копию для защиты от внешних изменений
        return new ArrayList<>(history);
    }
}
