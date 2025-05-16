package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {

    //выношу список методов для объекта Task;
    Task saveTask(Task task);

    ArrayList<Task> getAllTasks();

    Task getTaskById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    //выношу список методов для объекта Subtask;
    Subtask saveSubtask(Subtask subtask);

    ArrayList<Subtask> getAllSubtasks();

    Subtask getSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    //выношу список методов для объекта Epic;
    Epic saveEpic(Epic epic);

    ArrayList<Epic> getAllEpics();

    Epic getEpicById(int id);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    //Создаю метод для получения всех подзадач определённого эпика;
    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    //создаю метод для истории просмотров;
    ArrayList<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();

    boolean isTasksOverlap(Task task1, Task task2);

    boolean checkTasksOverlap(Task task1, Task task2);

    boolean checkTaskOverlapWithExisting(Task task);

    void deleteTasks(); // удаление всех задач

    void deleteSubtasks(); // удаление всех подзадач

    void deleteEpics(); // удаление всех эпиков
}