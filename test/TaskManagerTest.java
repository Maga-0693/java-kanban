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
    protected LocalDateTime baseTime;

    @BeforeEach
    public void setUp() {
        taskManager = getTaskManager();
        baseTime = LocalDateTime.now().withNano(0);

        // Инициализация задач с непересекающимися интервалами
        task = new Task("Задача 1", "Описание 1", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(baseTime.plusHours(1));

        epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        subtask = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW,
                Duration.ofMinutes(15), baseTime.plusHours(3));
    }

    protected abstract T getTaskManager();

    @Test
    void testGetPrioritizedTasks() {
        taskManager.saveEpic(epic); // Сначала сохраняем эпик

        // Корректируем время начала задачи, чтобы избежать пересечения
        task.setStartTime(baseTime.plusHours(1));
        taskManager.saveTask(task);

        // Корректируем время начала подзадачи, чтобы избежать пересечения
        subtask.setStartTime(baseTime.plusHours(3));
        taskManager.saveSubtask(subtask);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Должно быть 2 задачи в приоритетном списке");
        assertTrue(prioritizedTasks.contains(task), "Должна содержать задачу");
        assertTrue(prioritizedTasks.contains(subtask), "Должна содержать подзадачу");
    }

    @Test
    void testCheckTasksOverlap() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(baseTime.plusHours(1));

        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(baseTime.plusHours(1).plusMinutes(15)); // Пересекается с task1

        assertTrue(taskManager.isTasksOverlap(task1, task2), "Задачи должны пересекаться");
    }

    @Test
    void testCheckTaskOverlapWithExisting() {
        taskManager.saveTask(task);

        Task newTask = new Task("Новая задача", "Описание", Status.NEW);
        newTask.setDuration(Duration.ofMinutes(30));
        newTask.setStartTime(baseTime.plusHours(1).plusMinutes(15)); // Пересекается с task

        assertTrue(taskManager.checkTaskOverlapWithExisting(newTask),
                "Новая задача должна пересекаться с существующей");
    }

    @Test
    void testEpicStatusNew() {
        taskManager.saveEpic(epic);

        // Корректируем время начала подзадачи, чтобы избежать пересечения
        Subtask subtask1 = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW,
                Duration.ofMinutes(15), baseTime.plusHours(7));
        taskManager.saveSubtask(subtask1);

        assertEquals(Status.NEW, epic.getStatus(), "Статус должен быть NEW");
        assertNotNull(epic.getDuration(), "Продолжительность эпика не должна быть null");
        assertTrue(epic.getDuration().toMinutes() >= 0, "Продолжительность должна быть >= 0");
    }

    @Test
    void testEpicStatusDone() {
        taskManager.saveEpic(epic);

        // Корректируем время начала подзадачи, чтобы избежать пересечения
        Subtask subtask1 = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.DONE,
                Duration.ofMinutes(15), baseTime.plusHours(6));
        taskManager.saveSubtask(subtask1);

        assertEquals(Status.DONE, epic.getStatus(), "Статус должен быть DONE");
        assertNotNull(epic.getDuration(), "Продолжительность эпика не должна быть null");
    }

    @Test
    void testEpicStatusInProgress() {
        taskManager.saveEpic(epic);
        Subtask subtask1 = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW,
                Duration.ofMinutes(15), baseTime.plusHours(4));
        Subtask subtask2 = new Subtask(epic.getId(), "Подзадача 2", "Описание 2", Status.DONE,
                Duration.ofMinutes(15), baseTime.plusHours(5));
        taskManager.saveSubtask(subtask1);
        taskManager.saveSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен быть IN_PROGRESS");
        assertNotNull(epic.getDuration(), "Продолжительность эпика не должна быть null");
    }

}