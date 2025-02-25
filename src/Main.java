public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Task 1", "Description 1", Task.Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Task.Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description Epic 1", Task.Status.NEW);
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask(epic1.getId(), "Subtask 1", "Description Subtask 1", Task.Status.NEW);
        Subtask subtask2 = new Subtask(epic1.getId(), "Subtask 2", "Description Subtask 2", Task.Status.NEW);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic 2", "Description Epic 2", Task.Status.NEW);
        manager.createEpic(epic2);
        Subtask subtask3 = new Subtask(epic2.getId(), "Subtask 3", "Description Subtask 3", Task.Status.NEW);
        manager.createSubtask(subtask3);

        System.out.println("Все задачи:");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("Все подзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("Все эпики:");
        manager.getAllEpics().forEach(System.out::println);

        subtask1.setStatus(Task.Status.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Task.Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        System.out.println("Обновленные задачи:");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("Обновленные подзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("Обновленные эпики:");
        manager.getAllEpics().forEach(System.out::println);

        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic2.getId());

        System.out.println("Финальные задачи:");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("Финальные подзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("Финальные эпики:");
        manager.getAllEpics().forEach(System.out::println);
    }
}
