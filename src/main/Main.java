public class Main {
    public static void main(String[] args) {
        // 1. Setup managers
        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new InMemoryTaskManager(historyManager);

        // 2. Create two regular tasks
        Task task1 = taskManager.createTask(new Task("Task 1", "Simple task 1"));
        Task task2 = taskManager.createTask(new Task("Task 2", "Simple task 2"));

        // 3. Create an epic with three subtasks
        Epic epicWithSubtasks = taskManager.createEpic(new Epic("Epic 1", "Epic with subtasks"));
        Subtask sub1 = taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1", TaskStatus.NEW, epicWithSubtasks.getId()));
        Subtask sub2 = taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2", TaskStatus.NEW, epicWithSubtasks.getId()));
        Subtask sub3 = taskManager.createSubtask(new Subtask("Subtask 3", "Subtask 3", TaskStatus.NEW, epicWithSubtasks.getId()));

        // 4. Create an epic without subtasks
        Epic epicWithoutSubtasks = taskManager.createEpic(new Epic("Epic 2", "Epic without subtasks"));

        // 5. Simulate user viewing tasks in different orders
        // View task1, epicWithSubtasks, sub1
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epicWithSubtasks.getId());
        taskManager.getSubtaskById(sub1.getId());
        printHistory(historyManager, "History after viewing task1, epicWithSubtasks, sub1:");

        // View task2, sub2, sub3, epicWithoutSubtasks
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(sub2.getId());
        taskManager.getSubtaskById(sub3.getId());
        taskManager.getEpicById(epicWithoutSubtasks.getId());
        printHistory(historyManager, "History after viewing task2, sub2, sub3, epicWithoutSubtasks:");

        // View sub1 and epicWithSubtasks again (should move them to the end)
        taskManager.getSubtaskById(sub1.getId());
        taskManager.getEpicById(epicWithSubtasks.getId());
        printHistory(historyManager, "History after re-viewing sub1 and epicWithSubtasks:");

        // 6. Remove task2 from the system (and thus from history)
        taskManager.deleteTaskById(task2.getId());
        printHistory(historyManager, "History after deleting task2:");

        // 7. Remove epicWithSubtasks (and all its subtasks)
        taskManager.deleteEpicById(epicWithSubtasks.getId());
        printHistory(historyManager, "History after deleting epicWithSubtasks and its subtasks:");
    }

    private static void printHistory(HistoryManager historyManager, String header) {
        System.out.println(header);
        for (Task t : historyManager.getHistory()) {
            System.out.println("  " + t);
        }
        System.out.println();
    }
}
