import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import manager.TaskManager;
import manager.Managers;

import java.util.ArrayList;

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

        //вывод всех задач
        System.out.println("Все задачи:");
        ArrayList<Task> allTasks = manager.getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println(allTasks.get(i));
        }

        //вывод всех подзадач
        System.out.println("Все подзадачи:");
        ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
        for (int i = 0; i < allSubtasks.size(); i++) {
            System.out.println(allSubtasks.get(i));
        }

        //вывод всех эпиков
        System.out.println("Все эпики:");
        ArrayList<Epic> allEpics = manager.getAllEpics();
        for (int i = 0; i < allEpics.size(); i++) {
            System.out.println(allEpics.get(i));
        }

        //меняю статус первой задачи на ВЫПОЛНЕНО
        subtask1.setStatus(Status.DONE);
        //Обновляем подзадачу в менеджере. Это также обновляет статус эпика, к которому она относится
        manager.updateSubtask(subtask1);

        //меняю статус второй задачи на В ПРОЦЕССЕ
        subtask2.setStatus(Status.IN_PROGRESS);
        //обновляем подзадачу в менеджере
        manager.updateSubtask(subtask2);

        //вывожу все обновленные задачи
        System.out.println("Обновленные задачи:");
        allTasks = manager.getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println(allTasks.get(i));
        }

        //вывожу все обновленные подзадачи
        System.out.println("Обновленные подзадачи:");
        allSubtasks = manager.getAllSubtasks();
        for (int i = 0; i < allSubtasks.size(); i++) {
            System.out.println(allSubtasks.get(i));
        }

        //вывожу все обновленные эпики
        System.out.println("Обновленные эпики:");
        allEpics = manager.getAllEpics();
        for (int i = 0; i < allEpics.size(); i++) {
            System.out.println(allEpics.get(i));
        }

        //Удаляю задачу с ID task1
        manager.deleteTaskById(task1.getId());
        //Удаляем эпик с ID epic2. Все подзадачи, связанные с этим эпиком, также удаляются
        manager.deleteEpicById(epic2.getId());

        //вывожу все финальные задачи
        System.out.println("Финальные задачи:");
        allTasks = manager.getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println(allTasks.get(i));
        }

        //вывожу все финальные подзадачи
        System.out.println("Финальные подзадачи:");
        allSubtasks = manager.getAllSubtasks();
        for (int i = 0; i < allSubtasks.size(); i++) {
            System.out.println(allSubtasks.get(i));
        }

        //выожу все финальные эпики
        System.out.println("Финальные эпики:");
        allEpics = manager.getAllEpics();
        for (int i = 0; i < allEpics.size(); i++) {
            System.out.println(allEpics.get(i));
        }

        //смотрим задачи, чтобы заполнить историю
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        //вывожу историю просмотров
        System.out.println("История просмотров:");
        ArrayList<Task> history = manager.getHistory();
        for (int i = 0; i < history.size(); i++) {
            System.out.println(history.get(i));
        }
    }

    //сценарий для проверки
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
