package model;


import java.util.ArrayList;
import java.util.List;
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

    public void updateDurationAndTime(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            setEndTime(null);
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());

                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }

                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (latestEnd == null || subtaskEnd.isAfter(latestEnd)) {
                    latestEnd = subtaskEnd;
                }
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStart);
        setEndTime(latestEnd);
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