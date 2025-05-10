package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head; //первый элемент двусвязного списка
    private Node tail; //последний элемент двусвязного списка
    private final HashMap<Integer, Node> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        // Если задача есть в истории, то сначала удаляем старую версию
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        //добавляемв конец списка
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {

        return getTasks(); //Возвращаю список задач из истории
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id); //Нахожу узел по id
        if (node != null) {
            removeNode(node); //Удаляю узел из списка
        }
    }

    //Добавление задачи в конец двусвязного списка
    private void linkLast(Task task) {
        Node newNode = new Node(task);
        //Если список пустой, то новый узел становится и в начало и в конец
        if (tail == null) {
            head = tail = newNode;
        } else {
            //Иначе добавляем в конец списка
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        //сохраняем узел для быстрого доступа
        nodeMap.put(task.getId(), newNode);
    }

    //удаление узла из двусвязного списка
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

    //Переобразовываю двусвязный список в обычный список задач
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