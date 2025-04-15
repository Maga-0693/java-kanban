package manager;

import model.Task;

public class Node {
    Task task; //Ссылка на задачу
    Node prev; //Ссылка на предыдущий узел
    Node next; //Ссылка на следующий узел

    //Конструктор для создания узла к указанной задаче
    public Node(Task task) {

        this.task = task;
    }
}
