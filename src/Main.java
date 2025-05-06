import manager.Managers;
import manager.TaskManager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        //создание менеджера задач для управления всеми задачами, эпиками, подзадачами;
        TaskManager manager = Managers.getDefault();

        LocalDateTime baseTime = LocalDateTime.now();
        //создаем задачи 1 и 2
        Task task1 = new Task("Первая задача", "Первое описание", Status.NEW); //конструктор первой задачи
        task1.setDuration(Duration.ofMinutes(30)); //установка продолжительности 30 минут
        task1.setStartTime(baseTime.plusHours(1)); //начало через 1 час от текущего времени
        manager.saveTask(task1); //сохранение первой задачи в менеджере задач
        Task task2 = new Task("Вторая задача", "Второе описание", Status.NEW); //конструктор второй задачи
        task2.setDuration(Duration.ofHours(2)); //продолжительность 2 часа
        task2.setStartTime(baseTime.plusHours(4)); //начало через 4 часа
        manager.saveTask(task2); //сохранение второй задачи в менеджере задач


        //создание эпика
        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        manager.saveEpic(epic1); //сохранение эпика
        //создание подзадач, котороые связаны с эпиком через его ID
        Subtask subtask1 = new Subtask(
                epic1.getId(), // ID эпика к которому относится подзадача
                "Первая подзадача",
                "Описание первой подзадачи",
                Status.NEW,
                Duration.ofHours(3), //продолжительность 3 часа
                baseTime.plusDays(1).withHour(10).withMinute(0) //время начала - завтра в 10:00
        );
        manager.saveSubtask(subtask1);


        Subtask subtask2 = new Subtask(
                epic1.getId(),
                "Вторая подзадача",
                "Описание второй подзадачи",
                Status.NEW,
                Duration.ofHours(1), //1 час
                baseTime.plusDays(1).withHour(14).withMinute(0) //завтра в 14:00
        );

        manager.saveSubtask(subtask2);//сохранение подзадач в менеджере


        //создание второго эпика и подзадачи для него
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика", Status.NEW);
        manager.saveEpic(epic2);

        //вывод всех задач
        System.out.println("Все задачи");
        printAllTasks(manager);

        //тестирование истории просмотров
        System.out.println("\nТестирование истории");
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task1.getId());

        //вывод истории просмотров
        System.out.println("\n История просмотров");
        manager.getHistory().forEach(task ->
                System.out.println(" - " + task.getClass().getSimpleName() + ": " + task.getName()));

        //проверка пересечения задач по времени
        try {
            System.out.println("\nПопытка добавить пересекающуюся задачу");
            Task conflictTask = new Task("Конфликтная задача", "Должна вызвать ошибку", Status.NEW);
            conflictTask.setDuration(Duration.ofHours(1));
            conflictTask.setStartTime(baseTime.plusHours(1).plusMinutes(15)); //пересекается с задачей 1
            manager.saveTask(conflictTask);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    //метод для вывода всех задач
    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nОбычные задачи:");
        manager.getAllTasks().forEach(task ->
                System.out.printf("  %s (ID: %d) %s - %s%n",
                        task.getName(),
                        task.getId(),
                        task.getStartTime(),
                        task.getEndTime()));

        System.out.println("\nЭпики:");
        manager.getAllEpics().forEach(epic -> {
            System.out.printf("  %s (ID: %d) %s - %s%n",
                    epic.getName(),
                    epic.getId(),
                    epic.getStartTime(),
                    epic.getEndTime());

            System.out.println("  Подзадачи:");
            manager.getSubtasksByEpicId(epic.getId()).forEach(subtask ->
                    System.out.printf("    %s (ID: %d) %s - %s%n",
                            subtask.getName(),
                            subtask.getId(),
                            subtask.getStartTime(),
                            subtask.getEndTime()));
        });

        System.out.println("\nПриоритетный список задач:");
        manager.getPrioritizedTasks().forEach(task ->
                System.out.printf("  %s (ID: %d) %s - %s%n",
                        task.getName(),
                        task.getId(),
                        task.getStartTime(),
                        task.getEndTime()));
    }
}
