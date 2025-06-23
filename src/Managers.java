public class Managers {

    // Объявляем статические поля для хранения единственных экземпляров менеджеров
    private static TaskManager defaultTaskManager;
    private static HistoryManager defaultHistoryManager;

    /**
     * Возвращает стандартную (единственную) реализацию TaskManager.
     * Если менеджер задач еще не создан, он инициализируется вместе с менеджером истории.
     *
     * @return Единственный объект, реализующий интерфейс TaskManager.
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
     * Возвращает стандартную (единственную) реализацию HistoryManager.
     * Важно: Этот метод вызывает getDefault(), чтобы убедиться, что TaskManager
     * и HistoryManager инициализированы, и чтобы всегда возвращать тот же
     * экземпляр HistoryManager, который используется в getDefault().
     *
     * @return Единственный объект, реализующий интерфейс HistoryManager.
     */
    public static HistoryManager getDefaultHistory() {
        // Убедимся, что getDefault() был вызван, чтобы defaultHistoryManager был инициализирован.
        // Это гарантирует, что мы всегда получаем тот же экземпляр HistoryManager,
        // который связан с нашим TaskManager.
        getDefault(); // Вызываем, чтобы инициализировать оба менеджера, если они еще не инициализированы
        return defaultHistoryManager; // Возвращаем уже созданный экземпляр
    }
}
