import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of {@link HistoryManager} using a custom doubly-linked list and HashMap.
 * Maintains task view history in order of last access, without duplicates.
 * All operations run in O(1) time.
 * <p>Not thread-safe.</p>
 */
public class InMemoryHistoryManager implements HistoryManager {

    /** Maps task ID to its node in the linked list. */
    private final Map<Integer, Node> history = new HashMap<>();
    /** Head (oldest) node in the linked list. */
    private Node head;
    /** Tail (most recent) node in the linked list. */
    private Node tail;

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

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

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
     * Appends the given task to the end of the list and updates the map.
     *
     * @param task task to add
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
     * Removes the given node from the linked list.
     * Map cleanup is done by the caller.
     *
     * @param node node to remove
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
     * Node for doubly-linked list storage of history.
     */
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
