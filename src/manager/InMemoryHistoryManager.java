package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();
    @Override
    //создаю метод для добавления задач в историю
    public void add(Task task) {
        if (history.size() == 10) { // если размер истории равен 10, то
            history.remove(0); //удаляем самый старый элемент
        }
        history.add(task); // иначе добавлем новый элемент
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
