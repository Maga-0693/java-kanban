package test;

import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected LocalDateTime testTime;

    @BeforeEach
    public void setUp() {
        taskManager = getTaskManager();
        testTime = LocalDateTime.now();

        // Создаем задачи с разными временными интервалами
        task = new Task("Задача 1", "Описание 1", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(testTime.plusHours(1));

        epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        subtask = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW,
                Duration.ofMinutes(15), testTime.plusHours(2));
    }

    protected abstract T getTaskManager();

    @Test
    void testGetPrioritizedTasks() {
        taskManager.saveTask(task);
        taskManager.saveEpic(epic);
        taskManager.saveSubtask(subtask);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size());
        assertTrue(prioritizedTasks.contains(task));
        assertTrue(prioritizedTasks.contains(subtask));
    }

    @Test
    void testCheckTasksOverlap() {
        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(testTime.plusHours(1).plusMinutes(45));

        assertTrue(taskManager.checkTasksOverlap(task, task2));
    }

    @Test
    void testCheckTaskOverlapWithExisting() {
        taskManager.saveTask(task);
        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(testTime.plusHours(1).plusMinutes(15));

        assertTrue(taskManager.checkTaskOverlapWithExisting(task2));
    }

    @Test
    void testEpicStatusNew() {
        taskManager.saveEpic(epic);
        Subtask subtask1 = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW,
                Duration.ofMinutes(15), testTime.plusHours(3));
        taskManager.saveSubtask(subtask1);

        assertEquals(Status.NEW, epic.getStatus());
        assertNotNull(epic.getDuration());
    }

    @Test
    void testEpicStatusDone() {
        taskManager.saveEpic(epic);
        Subtask subtask1 = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.DONE, Duration.ofMinutes(15), testTime.plusHours(3));
        taskManager.saveSubtask(subtask1);

        assertEquals(Status.DONE, epic.getStatus());
        assertNotNull(epic.getDuration());
    }

    @Test
    void testEpicStatusInProgress() {
        taskManager.saveEpic(epic);
        Subtask subtask1 = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW, Duration.ofMinutes(50), testTime.plusHours(3));
        Subtask subtask2 = new Subtask(epic.getId(), "Подзадача 2", "Описание 2", Status.DONE, Duration.ofMinutes(5), testTime.plusHours(4));
        taskManager.saveSubtask(subtask1);
        taskManager.saveSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertNotNull(epic.getDuration());
    }
}