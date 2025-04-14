package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

    public class InMemoryHistoryManager implements HistoryManager {
        private Node head;
        private Node tail;
        private final HashMap<Integer, Node> nodeMap = new HashMap<>();

        @Override
        public void add(Task task) {
            if (nodeMap.containsKey(task.getId())) {
                removeNode(nodeMap.get(task.getId()));
            }
            linkLast(task);
        }

        @Override
        public ArrayList<Task> getHistory() {

            return getTasks();
        }

        @Override
        public void remove(int id) {
            Node node = nodeMap.get(id);
            if (node != null) {
                removeNode(node);
            }
        }

        private void linkLast(Task task) {
            Node newNode = new Node(task);
            if (tail == null) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            nodeMap.put(task.getId(), newNode);
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

            nodeMap.remove(node.task.getId());
        }

        private ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Node current = head;
            while (current != null) {
                tasks.add(current.task);
                current = current.next;
            }
            return tasks;
        }
    }
