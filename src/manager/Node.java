package manager;

import task.Task;

class Node {

    Task task;
    Node next;
    Node prev;

    public Node(Task task) {
        this.task = task;
    }
}
