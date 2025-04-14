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
    //проверка, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    void testTasksWithSameIdAreEqual() {
        // Создаем две задачи с одинаковым id
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("задача 2", "описание 2", Status.IN_PROGRESS);
        task2.setId(1);

        // Проверяем, что задачи равны друг другу
        assertEquals(task1, task2, "Задачи с одинаковым id равны");

        // Проверяем, что хэш-коды задач совпадают
        assertEquals(task1.hashCode(), task2.hashCode(), "Хэш-коды задач с одинаковым id совпадают");
    }

    //проверка, что наследники класса Task равны друг другу, если равен их id
    @Test
    void testEpicEqualityById() {
        // Создаем два эпика с одинаковым id
        Epic epic1 = new Epic("Эпик 1", "описание 1", Status.NEW);
        epic1.setId(1);

        Epic epic2 = new Epic("Эпик 2", "описание 2", Status.IN_PROGRESS);
        epic2.setId(1);

        // Проверяем, что эпики равны друг другу
        assertEquals(epic1, epic2, "Эпики с одинаковым id равны");

        // Проверяем, что хэш-коды эпиков совпадают
        assertEquals(epic1.hashCode(), epic2.hashCode(), "Хэш-коды эпиков с одинаковым id совпадают");
    }

    //тест на добавление задачи с правильным эпиком
    @Test
    void testAddSubtaskWithValidEpic() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("эпик 1", "описание 1", Status.NEW);
        taskManager.saveEpic(epic);

        Subtask subtask = new Subtask(epic.getId(), "подзадача 1", "описание 1", Status.NEW);
        taskManager.saveSubtask(subtask);

        // Проверяем, что подзадача добавлена и связана с эпиком
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача должна быть добавлена");
        assertEquals(epic.getId(), savedSubtask.getEpicId(), "Подзадача должна быть связана с эпиком");
    }

    //тест добавления задач
    @Test
    void testAddTask() {
        // Создаем менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();

        // Создаем задачу
        Task task = new Task("задача 1", "описание 1", Status.NEW);
        task.setId(1);

        // Добавляем задачу в менеджер
        taskManager.saveTask(task);

        // Проверяем, что задача добавлена
        Task savedTask = taskManager.getTaskById(1);
        assertNotNull(savedTask, "Задача должна быть добавлена в менеджер");
        assertEquals(task, savedTask, "Добавленная задача должна совпадать с сохраненной");
    }

    //тест добавления эпика
    @Test
    void testAddEpic() {
        // Создаем менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();

        // Создаем эпик
        Epic epic = new Epic("эпик 1", "описание 1", Status.NEW);
        epic.setId(1);

        // Добавляем эпик в менеджер
        taskManager.saveEpic(epic);

        // Проверяем, что эпик добавлен
        Epic savedEpic = taskManager.getEpicById(1);
        assertNotNull(savedEpic, "Эпик должен быть добавлен в менеджер");
        assertEquals(epic, savedEpic, "Добавленный эпик должен совпадать с сохраненным");
    }

    //тест добавления подзадачи
    @Test
    void testAddSubtask() {
        // Создаем менеджер задач
        TaskManager taskManager = new InMemoryTaskManager();

        // Создаем эпик
        Epic epic = new Epic("эпик 1", "описание 1", Status.NEW);
        epic.setId(1);
        taskManager.saveEpic(epic);

        // Создаем подзадачу
        Subtask subtask = new Subtask(1, "подзадача 1", "описание 1", Status.NEW);
        subtask.setId(2);

        // Добавляем подзадачу в менеджер
        taskManager.saveSubtask(subtask);

        // Проверяем, что подзадача добавлена
        Subtask savedSubtask = taskManager.getSubtaskById(2);
        assertNotNull(savedSubtask, "Подзадача должна быть добавлена в менеджер");
        assertEquals(subtask, savedSubtask, "Добавленная подзадача должна совпадать с сохраненной");

        // Проверяем, что подзадача связана с эпиком
        assertEquals(1, savedSubtask.getEpicId(), "Подзадача должна быть связана с эпиком");
    }

    //тест проверки статуса эпика при удалении подзадачи
    @Test
    void testEpicStatusAfterSubtaskDeletion() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("эпик 1", "описание 1", Status.NEW);
        taskManager.saveEpic(epic);

        Subtask subtask1 = new Subtask(epic.getId(), "подзадача 1", "описание 1", Status.NEW);
        taskManager.saveSubtask(subtask1);

        // Удаляем подзадачу
        taskManager.deleteSubtaskById(subtask1.getId());

        // Проверяем, что статус эпика NEW, так как подзадач нет
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW после удаления подзадачи");
    }
    @Test
    void testHistoryManager() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("задача 1", "описание 1", Status.NEW);
        taskManager.saveTask(task1);

        Task task2 = new Task("задача 2", "описание 2", Status.NEW);
        taskManager.saveTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task1, history.get(1), "Последняя задача в истории должна быть task1");
    }
}