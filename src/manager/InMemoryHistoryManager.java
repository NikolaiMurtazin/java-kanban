package manager;

import interfaces.HistoryManager;
import task.Task;

import java.util.HashMap;
import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> taskViewHistory = new HashMap<>();
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {

        if (containsKey(task.getId())) {
            Node current = taskViewHistory.get(task.getId());
            removeNode(current);
        }
        Node newNode = new Node(task, null, tail);
        linkLast(newNode);
        taskViewHistory.put(task.getId(), newNode);

    }

    @Override
    public void remove(int id) {
        if (containsKey(id)) {
            Node nodeToRemove = taskViewHistory.get(id);
            removeNode(nodeToRemove);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTask();
    }

    private void linkLast(Node newNode) {
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
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
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        taskViewHistory.remove(node.task.getId());
    }

    private boolean containsKey(int id) {
        return taskViewHistory.containsKey(id);
    }

     static class Node {

        Task task;
        Node next;
        Node prev;

         public Node(Task task, Node next, Node prev) {
             this.task = task;
             this.next = next;
             this.prev = prev;
         }
     }
}
