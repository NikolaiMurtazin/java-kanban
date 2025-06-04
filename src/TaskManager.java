import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер задач, отвечающий за создание, обновление, удаление
 * и получение задач, эпиков и подзадач.
 * Также управляет статусами эпиков на основе их подзадач.
 */
public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int nextId = 1;

    /**
     * Генерирует уникальный идентификатор для новой задачи.
     *
     * @return Уникальный ID.
     */
    private int generateId() {
        return nextId++;
    }

    // Методы для Task
    /**
     * Возвращает список всех обычных задач.
     *
     * @return Список задач ({@link Task}).
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Удаляет все обычные задачи.
     */
    public void removeAllTasks() {
        tasks.clear();
    }

    /**
     * Получает обычную задачу по её идентификатору.
     *
     * @param id Идентификатор задачи.
     * @return Объект {@link Task} или null, если задача не найдена.
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * Создаёт новую обычную задачу.
     * Задаче присваивается уникальный ID.
     *
     * @param task Объект {@link Task} для создания.
     * @return Созданная задача с присвоенным ID.
     */
    public Task createTask(Task task) {
        if (task == null) return null;
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    /**
     * Обновляет существующую обычную задачу.
     * Задача для обновления идентифицируется по ID внутри объекта task.
     *
     * @param task Объект {@link Task} с обновлёнными данными и корректным ID.
     */
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            // Можно добавить логирование или исключение, если задача для обновления не найдена
            return;
        }
        tasks.put(task.getId(), task);
    }

    /**
     * Удаляет обычную задачу по её идентификатору.
     *
     * @param id Идентификатор задачи для удаления.
     * @return Удалённый объект {@link Task} или null, если задача не найдена.
     */
    public Task deleteTaskById(int id) {
        return tasks.remove(id);
    }

    // Методы для Epic
    /**
     * Возвращает список всех эпиков.
     *
     * @return Список эпиков ({@link Epic}).
     */
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаляет все эпики.
     * При удалении эпиков также удаляются все связанные с ними подзадачи.
     */
    public void removeAllEpics() {
        // Сначала удалить все подзадачи, связанные с удаляемыми эпиками
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();
    }

    /**
     * Получает эпик по его идентификатору.
     *
     * @param id Идентификатор эпика.
     * @return Объект {@link Epic} или null, если эпик не найден.
     */
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    /**
     * Создаёт новый эпик.
     * Эпику присваивается уникальный ID. Статус эпика рассчитывается автоматически.
     *
     * @param epic Объект {@link Epic} для создания.
     * @return Созданный эпик с присвоенным ID и рассчитанным статусом.
     */
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic); // Статус эпика зависит от подзадач (которых пока нет)
        return epic;
    }

    /**
     * Обновляет существующий эпик.
     * Эпик для обновления идентифицируется по ID внутри объекта epic.
     * Обновляются только имя и описание эпика. Список подзадач и статус управляются менеджером.
     *
     * @param epic Объект {@link Epic} с обновлёнными данными (имя, описание) и корректным ID.
     */
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        // Статус пересчитывается, т.к. он не должен устанавливаться напрямую для эпика
        // updateEpicStatus(existingEpic); // Уже не нужно, т.к. подзадачи не менялись.
        // Статус эпика меняется только при изменении подзадач.
    }

    /**
     * Удаляет эпик по его идентификатору.
     * Также удаляет все подзадачи, связанные с этим эпиком.
     *
     * @param id Идентификатор эпика для удаления.
     * @return Удалённый объект {@link Epic} или null, если эпик не найден.
     */
    public Epic deleteEpicById(int id) {
        Epic epicToRemove = epics.remove(id);
        if (epicToRemove != null) {
            // Удалить все связанные подзадачи
            for (int subtaskId : new ArrayList<>(epicToRemove.getSubtaskIds())) { // Копия для безопасного удаления
                deleteSubtaskById(subtaskId); // Используем метод удаления подзадачи, чтобы он обновил эпик (хотя эпик уже удаляется)
                // или просто subtasks.remove(subtaskId);
            }
            // Очистим список subtaskIds в удаляемом эпике, хотя он уже удален из epics
            epicToRemove.clearSubtaskIds();
        }
        return epicToRemove;
    }


    // Методы для Subtask
    /**
     * Возвращает список всех подзадач.
     *
     * @return Список подзадач ({@link Subtask}).
     */
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Удаляет все подзадачи.
     * После удаления всех подзадач обновляются статусы соответствующих эпиков.
     */
    public void removeAllSubtasks() {
        // Собрать ID эпиков, которые нужно обновить
        List<Integer> epicIdsToUpdate = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (!epicIdsToUpdate.contains(subtask.getEpicId())) {
                epicIdsToUpdate.add(subtask.getEpicId());
            }
        }

        subtasks.clear();

        // Обновить статусы затронутых эпиков и очистить их списки подзадач
        for (int epicId : epicIdsToUpdate) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.clearSubtaskIds(); // Очищаем список ID подзадач в объекте эпика
                updateEpicStatus(epic);
            }
        }
    }

    /**
     * Получает подзадачу по её идентификатору.
     *
     * @param id Идентификатор подзадачи.
     * @return Объект {@link Subtask} или null, если подзадача не найдена.
     */
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    /**
     * Создаёт новую подзадачу.
     * Подзадаче присваивается уникальный ID. Она связывается с указанным эпиком.
     * Статус родительского эпика обновляется.
     *
     * @param subtask Объект {@link Subtask} для создания. ID эпика должен быть корректным.
     * @return Созданная подзадача с присвоенным ID или null, если родительский эпик не найден.
     */
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        Epic parentEpic = epics.get(subtask.getEpicId());
        if (parentEpic == null) {
            // Родительский эпик не найден
            return null;
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        parentEpic.addSubtaskId(subtask.getId());
        updateEpicStatus(parentEpic);
        return subtask;
    }

    /**
     * Обновляет существующую подзадачу.
     * Подзадача для обновления идентифицируется по ID внутри объекта subtask.
     * Если ID родительского эпика изменился, подзадача перепривязывается.
     * Статусы затронутых эпиков обновляются.
     *
     * @param subtask Объект {@link Subtask} с обновлёнными данными и корректным ID.
     */
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask == null) return; // Не должно произойти, если containsKey прошел

        Epic oldParentEpic = epics.get(oldSubtask.getEpicId());
        Epic newParentEpic = epics.get(subtask.getEpicId());

        if (newParentEpic == null) {
            // Новый родительский эпик не найден, обновление невозможно в таком виде
            // или нужно решить, как обрабатывать (например, не менять эпик)
            return;
        }

        // Если эпик подзадачи изменился
        if (oldSubtask.getEpicId() != subtask.getEpicId()) {
            if (oldParentEpic != null) {
                oldParentEpic.removeSubtaskId(subtask.getId());
                updateEpicStatus(oldParentEpic);
            }
            newParentEpic.addSubtaskId(subtask.getId());
        }

        subtasks.put(subtask.getId(), subtask); // Обновляем саму подзадачу в мапе
        updateEpicStatus(newParentEpic); // Обновляем статус нового (или текущего, если не менялся) эпика
    }

    /**
     * Удаляет подзадачу по её идентификатору.
     * Статус родительского эпика обновляется.
     *
     * @param id Идентификатор подзадачи для удаления.
     * @return Удалённый объект {@link Subtask} или null, если подзадача не найдена.
     */
    public Subtask deleteSubtaskById(int id) {
        Subtask subtaskToRemove = subtasks.remove(id);
        if (subtaskToRemove != null) {
            Epic parentEpic = epics.get(subtaskToRemove.getEpicId());
            if (parentEpic != null) {
                parentEpic.removeSubtaskId(id);
                updateEpicStatus(parentEpic);
            }
        }
        return subtaskToRemove;
    }

    // Дополнительные методы
    /**
     * Получает список всех подзадач указанного эпика.
     *
     * @param epicId Идентификатор эпика.
     * @return Список подзадач ({@link Subtask}) для данного эпика или пустой список, если эпик не найден или у него нет подзадач.
     */
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>(); // или null, или бросить исключение
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }


    /**
     * Обновляет статус эпика на основе статусов его подзадач.
     * Правила:
     * - Если у эпика нет подзадач, статус NEW.
     * - Если все подзадачи имеют статус NEW, статус эпика NEW.
     * - Если все подзадачи имеют статус DONE, статус эпика DONE.
     * - Во всех остальных случаях статус эпика IN_PROGRESS.
     *
     * @param epic Эпик, статус которого нужно обновить.
     */
    private void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
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
                // Обработка случая, если subtaskId есть в epic, но самой subtask нет в subtasks.
                // Это может указывать на несогласованность данных.
                // Для простоты пока проигнорируем, но в реальной системе это надо логировать/исправлять.
            }
        }

        if (hasInProgress || (newCount > 0 && doneCount > 0)) { // Если есть хоть одна IN_PROGRESS или есть и NEW и DONE
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (doneCount == subtaskIds.size()) { // Все DONE
            epic.setStatus(TaskStatus.DONE);
        } else if (newCount == subtaskIds.size()) { // Все NEW (или нет других кроме NEW)
            epic.setStatus(TaskStatus.NEW);
        } else { // По идее, этот случай не должен достигаться, если логика выше верна,
            // но на всякий случай можно поставить IN_PROGRESS как наиболее общий
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
