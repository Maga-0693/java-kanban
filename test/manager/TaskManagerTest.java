package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected LocalDateTime baseTime;

    @BeforeEach
    public void setUp() {
        taskManager = getTaskManager();
        baseTime = LocalDateTime.now().withNano(0);

        task = new Task("Задача 1", "Описание 1", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(baseTime.plusHours(1));

        epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        subtask = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW,
                Duration.ofMinutes(15), baseTime.plusHours(3));
    }

    protected abstract T getTaskManager();

    @Test
    void testCheckTasksOverlap() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(baseTime.plusHours(1));

        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(baseTime.plusHours(1).plusMinutes(15));

        assertTrue(taskManager.isTasksOverlap(task1, task2), "Задачи должны пересекаться");
    }

    @Test
    void testCheckTaskOverlapWithExisting() {
        taskManager.saveTask(task);

        Task newTask = new Task("Новая задача", "Описание", Status.NEW);
        newTask.setDuration(Duration.ofMinutes(30));
        newTask.setStartTime(baseTime.plusHours(1).plusMinutes(15));

        assertTrue(taskManager.checkTaskOverlapWithExisting(newTask),
                "Новая задача должна пересекаться с существующей");
    }
}