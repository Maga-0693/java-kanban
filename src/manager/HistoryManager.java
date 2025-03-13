package manager;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add (Task task); //метод, который помечает задачи как просмотренные
    ArrayList<Task> getHistory(); //метод возвращающий список задач истории
}
