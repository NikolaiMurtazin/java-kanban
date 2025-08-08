package history;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of {@link HistoryManager} using a custom doubly-linked list and {@link HashMap}.
 * <p>
 * Maintains a history of viewed tasks in order of last access. Ensures uniqueness:
 * re-accessed tasks are moved to the end of the list. All core operations (add, remove, get) run in O(1) time.
 * <p><strong>Note:</strong> This class is <em>not thread-safe</em>.</p>
 */
public class InMemoryHistoryManager implements HistoryManager {

    /** Maps task ID to its corresponding node in the list for O(1) lookup and removal. */
    private final Map<Integer, Node> history = new HashMap<>();
    /** Head of the doubly-linked list (oldest task). */
    private Node head;
    /** Tail of the doubly-linked list (most recently accessed task). */
    private Node tail;

    /**
     * Adds a task to the history. If the task already exists in history,
     * it is moved to the end (most recent).
     *
     * @param task the task to add (ignored if {@code null})
     */
    @Override
    public void add(Task task) {
        if (task == null) return;
        int id = task.getId();
        Node existing = history.remove(id);
        if (existing != null) {
            removeNode(existing);
        }
        linkLast(task);
    }

    /**
     * Removes a task from history by its ID.
     *
     * @param id the ID of the task to remove
     */
    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    /**
     * Returns the list of tasks in the order they were last accessed.
     * The oldest task is first, and the most recently viewed is last.
     *
     * @return a list of tasks in access order
     */
    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

    /**
     * Appends a task to the end of the list and adds it to the history map.
     *
     * @param task the task to add
     */
    private void linkLast(Task task) {
        Node newNode = new Node(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        history.put(task.getId(), newNode);
    }

    /**
     * Removes the given node from the doubly-linked list.
     * Note: this method does not remove the node from the map —
     * this should be done by the caller.
     *
     * @param node the node to remove
     */
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
    }

    /**
     * A node in the doubly-linked list used to store the history.
     * Contains links to both previous and next nodes for efficient removal.
     */
    private static class Node {
        /** The task stored in this node. */
        Task task;
        /** Link to the previous node in the list. */
        Node prev;
        /** Link to the next node in the list. */
        Node next;

        /**
         * Creates a new node for the doubly-linked list.
         *
         * @param prev the previous node
         * @param task the task stored in this node
         * @param next the next node
         */
        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
