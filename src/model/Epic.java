package model;

import manager.Managers;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds; //id подзадач
    private LocalDateTime endTime; //время окончания

    //конструктор для создания эпика
    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subtaskIds = new ArrayList<>(); //инициализация списка подзадач
    }

    //получение списка id подзадач
    public ArrayList<Integer> getSubtaskIds() {

        return new ArrayList<>(subtaskIds); //возвращаем копию списка
    }

    //добавление id подзадачи в список
    public void addSubtaskId(int subtaskId) {

        subtaskIds.add(subtaskId); //добавляем id подзадачи
    }

    //удаление id подзадачи из списка
    public void removeSubtaskId(int subtaskId) {

        subtaskIds.remove(Integer.valueOf(subtaskId)); //удаляем id подзадачи
    }

    public LocalDateTime getEndTime() {

        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {

        this.endTime = endTime;
    }

    public void updateEpicDurationAndTime() {
        if (subtaskIds.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            setEndTime(null);
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = Managers.getDefault().getSubtaskById(subtaskId);
            if (subtask != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
                if (earliestStartTime == null || subtask.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = subtask.getStartTime();
                }
                if (latestEndTime == null || subtask.getEndTime().isAfter(latestEndTime)) {
                    latestEndTime = subtask.getEndTime();
                }
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStartTime);
        setEndTime(latestEndTime);
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
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + endTime +
                '}';
    }
}