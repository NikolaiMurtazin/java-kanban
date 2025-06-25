/**
 * Утилитарный класс, отвечающий за создание и предоставление
 * стандартных (дефолтных) реализаций менеджеров приложения:
 * {@link TaskManager} и {@link HistoryManager}.
 * Реализует паттерн "Синглтон" для этих менеджеров,
 * гарантируя, что по всему приложению используется один и тот же экземпляр.
 */
public class Managers {

    // Объявляем статические поля для хранения единственных экземпляров менеджеров.
    // Они будут инициализированы при первом вызове getDefault() или getDefaultHistory().
    private static TaskManager defaultTaskManager;
    private static HistoryManager defaultHistoryManager;

    /**
     * Возвращает единственный (синглтон) экземпляр {@link TaskManager}.
     * При первом вызове метода, менеджер задач и связанный с ним
     * менеджер истории инициализируются. Последующие вызовы
     * всегда возвращают тот же самый экземпляр.
     *
     * @return Единственный объект, реализующий интерфейс {@link TaskManager}.
     */
    public static TaskManager getDefault() {
        // Проверяем, был ли менеджер задач уже создан.
        // Если нет, создаем его вместе с менеджером истории.
        if (defaultTaskManager == null) {
            defaultHistoryManager = new InMemoryHistoryManager(); // Создаем единственный экземпляр HistoryManager
            defaultTaskManager = new InMemoryTaskManager(defaultHistoryManager); // Передаем его в TaskManager
        }
        return defaultTaskManager; // Возвращаем уже созданный экземпляр
    }

    /**
     * Возвращает единственный (синглтон) экземпляр {@link HistoryManager}.
     * Этот метод гарантирует, что возвращаемый менеджер истории
     * является тем же, который используется внутри {@link TaskManager},
     * возвращаемого методом {@link #getDefault()}.
     *
     * @return Единственный объект, реализующий интерфейс {@link HistoryManager}.
     */
    public static HistoryManager getDefaultHistory() {
        // Убедимся, что getDefault() был вызван, чтобы defaultHistoryManager
        // был инициализирован. Это гарантирует, что мы всегда получаем
        // тот же экземпляр HistoryManager, который связан с нашим TaskManager.
        getDefault(); // Вызываем, чтобы инициализировать оба менеджера, если они еще не инициализированы
        return defaultHistoryManager; // Возвращаем уже созданный экземпляр
    }
}
