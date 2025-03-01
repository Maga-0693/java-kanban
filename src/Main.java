import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Первая задача", "Первое описание", Status.NEW);
        Task task2 = new Task("Вторая задача", "Второе описание", Status.NEW);

        manager.saveTask(task1);
        manager.saveTask(task2);

        Epic epic1 = new Epic("Первый эпик", "Описание первого эпика", Status.NEW);
        manager.saveEpic(epic1);
        Subtask subtask1 = new Subtask(epic1.getId(), "Первая подзадача", "Описание первой подзадачи", Status.NEW);
        Subtask subtask2 = new Subtask(epic1.getId(), "Вторая подзадача", "Описание второй подзадачи", Status.NEW);
        manager.saveSubtask(subtask1);
        manager.saveSubtask(subtask2);

        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика", Status.NEW);
        manager.saveEpic(epic2);
        Subtask subtask3 = new Subtask(epic2.getId(), "Третья подзадача", "Описание третьей подзадачи", Status.NEW);
        manager.saveSubtask(subtask3);

        System.out.println("Все задачи:");
        ArrayList<Task> allTasks = manager.getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println(allTasks.get(i));
        }

        System.out.println("Все подзадачи:");
        ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
        for (int i = 0; i < allSubtasks.size(); i++) {
            System.out.println(allSubtasks.get(i));
        }

        System.out.println("Все эпики:");
        ArrayList<Epic> allEpics = manager.getAllEpics();
        for (int i = 0; i < allEpics.size(); i++) {
            System.out.println(allEpics.get(i));
        }

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        System.out.println("Обновленные задачи:");
        allTasks = manager.getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println(allTasks.get(i));
        }

        System.out.println("Обновленные подзадачи:");
        allSubtasks = manager.getAllSubtasks();
        for (int i = 0; i < allSubtasks.size(); i++) {
            System.out.println(allSubtasks.get(i));
        }

        System.out.println("Обновленные эпики:");
        allEpics = manager.getAllEpics();
        for (int i = 0; i < allEpics.size(); i++) {
            System.out.println(allEpics.get(i));
        }

        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("Финальные задачи:");
        allTasks = manager.getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println(allTasks.get(i));
        }

        System.out.println("Финальные подзадачи:");
        allSubtasks = manager.getAllSubtasks();
        for (int i = 0; i < allSubtasks.size(); i++) {
            System.out.println(allSubtasks.get(i));
        }

        System.out.println("Финальные эпики:");
        allEpics = manager.getAllEpics();
        for (int i = 0; i < allEpics.size(); i++) {
            System.out.println(allEpics.get(i));
        }
    }
}
