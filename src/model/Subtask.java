package model;

public class Subtask extends Task {
    //ID эпика, к которому относится подзадача
    private int epicId;

    //Конструктор для создания подзадачи
    public Subtask(int epicId, String name, String description, Status status) {
        super(name, description, status); //Вызов конструктора родительского класса Task
        this.epicId = epicId; //Установка ID эпика
    }

    //Получение ID эпика
    public int getEpicId() {
        return epicId;
    }

    //Установка ID эпика
    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    //Переопределение метода toString для вывода информации о подзадаче
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
