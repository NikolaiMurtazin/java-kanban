import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реализация интерфейса {@link TaskManager}, которая хранит все задачи,
 * эпики и подзадачи в оперативной памяти с использованием {@link Map}.
 * Отвечает за управление жизненным циклом задач, их идентификацией,
 * а также за автоматический расчет статусов эпиков на основе их подзадач.
 * Интегрируется с {@link HistoryManager} для отслеживания просмотренных задач.
 */
public class InMemoryTaskManager implements TaskManager {

    // Хранилища для различных типов задач
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int nextId; // Счетчик для генерации уникальных ID

    // Менеджер истории, предоставляется через конструктор
    private final HistoryManager historyManager;

    /**
     * Конструктор менеджера задач в памяти.
     * Инициализирует внутренние хранилища и устанавливает начальный ID.
     * Требует предоставления {@link HistoryManager} для управления историей просмотров.
     *
     * @param historyManager Реализация {@link HistoryManager} для использования.
     */
    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.nextId = 1; // ID обычно начинается с 1
        this.historyManager = historyManager;
    }

    /**
     * Генерирует и возвращает следующий уникальный идентификатор.
     * Этот ID используется для всех типов задач (Task, Epic, Subtask).
     *
     * @return Уникальный целочисленный идентификатор.
     */
    private int generateId() {
        return nextId++;
    }

    // --- Методы для работы с обычными задачами (Task) ---

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    /**
     * {@inheritDoc}
     * Дополнительно добавляет просмотренную задачу в историю через {@link HistoryManager}.
     */
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task); // Добавляем задачу в историю при каждом просмотре
        }
        return task;
    }

    /**
     * {@inheritDoc}
     * Задаче присваивается новый уникальный ID.
     */
    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            // Можно добавить логирование или выбросить исключение для случая,
            // когда задача для обновления не найдена по ID.
            return;
        }
        tasks.put(task.getId(), task); // Заменяем старый объект новым с тем же ID
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // --- Методы для работы с эпиками (Epic) ---

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * {@inheritDoc}
     * При удалении всех эпиков также удаляются все связанные с ними подзадачи.
     */
    @Override
    public void removeAllEpics() {
        // Удаляем все подзадачи, так как они не могут существовать без эпиков.
        subtasks.clear(); // Очищаем хранилище подзадач
        epics.clear(); // Очищаем хранилище эпиков
    }

    /**
     * {@inheritDoc}
     * Дополнительно добавляет просмотренный эпик в историю через {@link HistoryManager}.
     */
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic); // Добавляем эпик в историю при просмотре
        }
        return epic;
    }

    /**
     * {@inheritDoc}
     * Эпику присваивается новый уникальный ID.
     * Статус эпика обновляется после создания, учитывая, что подзадач еще нет.
     */
    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic); // При создании эпика без подзадач, его статус будет NEW
        return epic;
    }

    /**
     * {@inheritDoc}
     * Обновляются только имя и описание эпика. Список подзадач и статус
     * эпика управляются внутренне (через {@code updateEpicStatus})
     * и не должны устанавливаться напрямую.
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(epic.getId());
        // Обновляем только поля, которые могут быть изменены пользователем
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        // Статус эпика обновляется только при изменении его подзадач,
        // поэтому здесь вызов updateEpicStatus() не нужен.
    }

    /**
     * {@inheritDoc}
     * При удалении эпика также удаляются все подзадачи, которые были
     * связаны с ним.
     */
    @Override
    public Epic deleteEpicById(int id) {
        Epic epicToRemove = epics.remove(id);
        if (epicToRemove != null) {
            // Создаем копию списка ID подзадач, чтобы избежать ConcurrentModificationException
            for (Integer subtaskId : new ArrayList<>(epicToRemove.getSubtaskIds())) {
                subtasks.remove(subtaskId); // Удаляем подзадачу по ID
            }
            // Очищаем список subtaskIds в удаляемом объекте Epic (хотя он уже удален из Map)
            epicToRemove.clearSubtaskIds();
        }
        return epicToRemove;
    }


    // --- Методы для работы с подзадачами (Subtask) ---

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * {@inheritDoc}
     * При удалении всех подзадач, статусы соответствующих эпиков обновляются.
     */
    @Override
    public void removeAllSubtasks() {
        // Сначала собираем ID всех эпиков, чьи подзадачи будут удалены,
        // чтобы потом обновить их статусы.
        List<Integer> epicIdsToUpdate = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (!epicIdsToUpdate.contains(subtask.getEpicId())) {
                epicIdsToUpdate.add(subtask.getEpicId());
            }
        }

        subtasks.clear(); // Удаляем все подзадачи

        // Обновляем статусы затронутых эпиков и очищаем их списки подзадач
        for (int epicId : epicIdsToUpdate) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.clearSubtaskIds(); // Очищаем список ID подзадач в объекте эпика
                updateEpicStatus(epic); // Пересчитываем статус эпика (теперь без подзадач он NEW)
            }
        }
    }

    /**
     * {@inheritDoc}
     * Дополнительно добавляет просмотренную подзадачу в историю через {@link HistoryManager}.
     */
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask); // Добавляем подзадачу в историю при просмотре
        }
        return subtask;
    }

    /**
     * {@inheritDoc}
     * Подзадаче присваивается новый уникальный ID. Она связывается с указанным эпиком.
     * Статус родительского эпика обновляется.
     *
     * @param subtask Объект {@link Subtask} для создания. ID эпика должен быть корректным.
     * @return Созданная подзадача с присвоенным ID или {@code null}, если родительский эпик не найден.
     */
    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        Epic parentEpic = epics.get(subtask.getEpicId());
        if (parentEpic == null) {
            // Нельзя создать подзадачу без существующего родительского эпика
            System.out.println("Ошибка: Родительский эпик с ID " + subtask.getEpicId() + " не найден.");
            return null;
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        parentEpic.addSubtaskId(subtask.getId()); // Добавляем ID подзадачи в список эпика
        updateEpicStatus(parentEpic); // Обновляем статус родительского эпика
        return subtask;
    }

    /**
     * {@inheritDoc}
     * Обновляет подзадачу, её имя, описание, статус.
     * Если ID родительского эпика изменился, подзадача перепривязывается,
     * а статусы затронутых эпиков обновляются.
     *
     * @param subtask Объект {@link Subtask} с обновлёнными данными и корректным ID.
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask == null) return; // Не должно произойти, если containsKey проверен

        Epic oldParentEpic = epics.get(oldSubtask.getEpicId());
        Epic newParentEpic = epics.get(subtask.getEpicId());

        if (newParentEpic == null) {
            // Новый родительский эпик не найден, обновление невозможно или некорректно
            System.out.println("Ошибка: Новый родительский эпик с ID " + subtask.getEpicId() + " не найден для подзадачи.");
            return;
        }

        // Если эпик подзадачи изменился
        if (oldSubtask.getEpicId() != subtask.getEpicId()) {
            if (oldParentEpic != null) {
                oldParentEpic.removeSubtaskId(oldSubtask.getId());
                updateEpicStatus(oldParentEpic);
            }
            newParentEpic.addSubtaskId(subtask.getId());
        }

        subtasks.put(subtask.getId(), subtask); // Обновляем саму подзадачу в мапе
        updateEpicStatus(newParentEpic); // Обновляем статус нового (или текущего, если не менялся) эпика
    }

    /**
     * {@inheritDoc}
     * Удаляет подзадачу по её идентификатору.
     * После удаления статус родительского эпика обновляется.
     */
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtaskToRemove = subtasks.remove(id);
        if (subtaskToRemove != null) {
            Epic parentEpic = epics.get(subtaskToRemove.getEpicId());
            if (parentEpic != null) {
                parentEpic.removeSubtaskId(id); // Удаляем ID подзадачи из списка родительского эпика
                updateEpicStatus(parentEpic); // Обновляем статус родительского эпика
            }
        }
    }

    // --- Дополнительные методы ---

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>(); // Если эпик не найден, возвращаем пустой список
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) { // Убеждаемся, что подзадача действительно существует
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    /**
     * Внутренний метод для пересчета и обновления статуса эпика
     * на основе статусов его текущих подзадач.
     * Правила расчета статуса:
     * <ul>
     * <li>Если у эпика нет подзадач, его статус становится {@link TaskStatus#NEW}.</li>
     * <li>Если все подзадачи имеют статус {@link TaskStatus#NEW}, статус эпика {@link TaskStatus#NEW}.</li>
     * <li>Если все подзадачи имеют статус {@link TaskStatus#DONE}, статус эпика {@link TaskStatus#DONE}.</li>
     * <li>Во всех остальных случаях (есть подзадачи с разными статусами, или есть хотя бы одна {@link TaskStatus#IN_PROGRESS}),
     * статус эпика становится {@link TaskStatus#IN_PROGRESS}.</li>
     * </ul>
     *
     * @param epic Эпик, статус которого необходимо обновить.
     */
    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW); // Если подзадач нет, эпик NEW
            return;
        }

        int newCount = 0;
        int doneCount = 0;
        boolean hasInProgress = false;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) { // Подзадача должна существовать
                switch (subtask.getStatus()) {
                    case NEW:
                        newCount++;
                        break;
                    case DONE:
                        doneCount++;
                        break;
                    case IN_PROGRESS:
                        hasInProgress = true;
                        break;
                }
            } else {
                // Если подзадача, указанная в списке эпика, не найдена в хранилище подзадач.
                // Это может указывать на несогласованность данных.
            }
        }

        if (hasInProgress || (newCount > 0 && doneCount > 0)) {
            // Если есть хотя бы одна IN_PROGRESS, или есть и NEW, и DONE задачи
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (newCount == subtaskIds.size()) {
            // Все подзадачи NEW
            epic.setStatus(TaskStatus.NEW);
        } else if (doneCount == subtaskIds.size()) {
            // Все подзадачи DONE
            epic.setStatus(TaskStatus.DONE);
        } else {
            // Резервный случай, если ни одно из условий выше не сработало.
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
