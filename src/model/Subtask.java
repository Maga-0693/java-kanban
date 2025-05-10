package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    //id эпика, к которому относится подзадача
    private int epicId;

    //конструктор для создания подзадачи
    public Subtask(int epicId, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status);
        this.epicId = epicId; //установка id эпика
        setDuration(duration);
        setStartTime(startTime);
    }

    //получение id эпика
    public int getEpicId() {

        return epicId;
    }

    //установка id эпика
    public void setEpicId(int epicId) {

        this.epicId = epicId;
    }

    //переопределение метода toString для вывода информации о подзадаче
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}