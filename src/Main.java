import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import manager.TaskManager;
import manager.Managers;
import manager.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        //создание менеджера задач для управления всеми задачами, эпиками, подзадачами;
        TaskManager manager = Managers.getDefault();
        //создание объекта Менеджер задач !обращаемся уже к интерфейсу TaskManager!; создаем
        // уже менеджер задач через Managers, точнее меняем его на Managers

        //создаем задачи 1 и 2
        Task task1 = new Task("Первая задача", "Первое описание", Status.NEW); //конструктор первой задачи
        Task task2 = new Task("Вторая задача", "Второе описание", Status.NEW); //конструктор второй задачи

        //сохранение задач в менеджере зада с присвоением ID
        manager.saveTask(task1); //сохранение первой задачи в менеджере задач
        manager.saveTask(task2); //сохранение второй задачи в менеджере задач

        //создание эпика
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        //сохранение эпика
        manager.saveEpic(epic1);
        //создание двух подзадач, котороые связаны с эпиком через его ID
        Subtask subtask1 = new Subtask(epic1.getId(), "Первая подзадача", "Описание первой подзадачи", Status.NEW);
        Subtask subtask2 = new Subtask(epic1.getId(), "Вторая подзадача", "Описание второй подзадачи", Status.NEW);
        //сохранение подзадач в менеджере
        manager.saveSubtask(subtask1);
        manager.saveSubtask(subtask2);

        //создание второго эпика и подзадачи для него
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика", Status.NEW);
        manager.saveEpic(epic2);
        Subtask subtask3 = new Subtask(epic2.getId(), "Третья подзадача", "Описание третьей подзадачи", Status.NEW);
        manager.saveSubtask(subtask3);

        printAllTasks(manager);

        // Тестирование диспетчера задач
        try {
            File file = File.createTempFile("задачи", ".csv");
            FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

            // Добавляем задачи в файловый менеджер
            fileManager.saveTask(new Task("Файловая задача", "Описание", Status.NEW));
            fileManager.saveEpic(new Epic("Файловый эпик", "Описание", Status.NEW));

            // Восстанавливаем из файла
            FileBackedTaskManager restoredManager = FileBackedTaskManager.loadFromFile(file);
            printAllTasks(restoredManager);
        } catch (IOException e) {
            System.out.println("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    //вывожу все задачи
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(System.out::println);

        //вывожу все эпики
        System.out.println("Эпики:");
        manager.getAllEpics().forEach(epic -> {
            System.out.println(epic);
            manager.getSubtasksByEpicId(epic.getId()).forEach(subtask ->
                    System.out.println("--> " + subtask));
        });

        //вывод всех подзадач
        System.out.println("Подзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        //вывожу историю просмотров
        System.out.println("История:");
        manager.getHistory().forEach(System.out::println);
    }
}
