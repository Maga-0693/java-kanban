package model;

import java.util.ArrayList;

public class Epic extends Task {
    //Список ID подзадач, которые относятся к этому эпику
    private ArrayList<Integer> subtaskIds;

    //Конструктор для создания эпика
    public Epic(String name, String description, Status status) {
        super(name, description, status); //Вызов конструктора родительского класса Task
        this.subtaskIds = new ArrayList<>(); //Инициализация списка подзадач
    }

    //Получение списка ID подзадач
    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds); //Возвращаем копию списка
    }

    //Добавление ID подзадачи в список
    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId); //Добавляем ID подзадачи
    }

    //Удаление ID подзадачи из списка
    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId)); //Удаляем ID подзадачи
    }

    //Переопределение метода toString для вывода информации об эпике
    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
