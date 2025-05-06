package test;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Task;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void testTasksWithSameIdAreEqual() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);

        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);

        assertEquals(task1, task2, "Задачи с одинаковыми ID равны");
        assertEquals(task1.hashCode(), task2.hashCode(), "Хэш-коды задач с одинаковыми ID равны");
    }

    @Test
    void testEpicEqualityById() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1", Status.NEW);

        Epic epic2 = new Epic("Эпик 2", "Описание 2", Status.IN_PROGRESS);

        assertEquals(epic1, epic2, "Эпики с одинаковыми ID равны");
        assertEquals(epic1.hashCode(), epic2.hashCode(), "Хэш-коды эпиков с одинаковыми ID равны");
    }

    @Test
    void testAddSubtaskWithValidEpic() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW, null, null);
        taskManager.saveSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача добавлена");
        assertEquals(epic.getId(), savedSubtask.getEpicId(), "Подзадача связана с эпиком");
    }

    @Test
    void testAddTask() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Задача 1", "Описание 1", Status.NEW);

        taskManager.saveTask(task);

        Task savedTask = taskManager.getTaskById(1);
        assertNotNull(savedTask, "Задача добавлена");
        assertEquals(task, savedTask, "Сохраненная задача должна быть равна первоначальной");
    }

    @Test
    void testAddEpic() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        epic.setId(1);

        taskManager.saveEpic(epic);

        Epic savedEpic = taskManager.getEpicById(1);
        assertNotNull(savedEpic, "Эпик добавлен");
        assertEquals(epic, savedEpic, "Сохраненный эпик должн быть равен первоначальному");
    }

    @Test
    void testAddSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask(1, "Подзадача 1", "Описание 1", Status.NEW, null, null);

        taskManager.saveSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtaskById(2);
        assertNotNull(savedSubtask, "Подзадача добавлена");
        assertEquals(subtask, savedSubtask, "Сохраненная подзадача должна быть равна первоначальной");
        assertEquals(1, savedSubtask.getEpicId(), "Подзадача должна быть связана с эпиком");
    }

    @Test
    void testEpicStatusAfterSubtaskDeletion() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Эпик 1", "Описание 1", Status.NEW);
        taskManager.saveEpic(epic);

        Subtask subtask1 = new Subtask(epic.getId(), "Подзадача 1", "Описание 1", Status.NEW, null, null);
        taskManager.saveSubtask(subtask1);

        taskManager.deleteSubtaskById(subtask1.getId());

        assertEquals(Status.NEW, epic.getStatus(), "После удаления подзадачи, стату эпика должен быть NEW");
    }

    @Test
    void testHistoryManager() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        taskManager.saveTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        taskManager.saveTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "В истории должно быть 2 задачи");
        assertEquals(task1, history.get(1), "Последней в истории должна быть задача 1");
    }
}