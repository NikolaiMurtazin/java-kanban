package manager;

import interfaces.HistoryManager;
import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> taskViewHistory = new HashMap<>();
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        head = null;
        tail = null;
    }

    @Override
    public void add(Task task) {

        if (taskViewHistory.containsKey(task.getId())) {
            Node current = taskViewHistory.get(task.getId());
            removeNode(current);
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        taskViewHistory.put(task.getId(), newNode);

    }

    @Override
    public void remove(int id) {
        if (taskViewHistory.containsKey(id)) {
            Node nodeToRemove = taskViewHistory.get(id);
            removeNode(nodeToRemove);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTask();
    }

    private void linkLast(Node newNode) {
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    private ArrayList<Task> getTask() {
        ArrayList<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = head.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        taskViewHistory.remove(node.task.getId());
    }
}
