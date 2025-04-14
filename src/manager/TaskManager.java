package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {

    Task saveTask(Task task);//выношу список методов для объекта Task;

    ArrayList<Task> getAllTasks();

    Task getTaskById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    Subtask saveSubtask(Subtask subtask);//выношу список методов для объекта Subtask;

    ArrayList<Subtask> getAllSubtasks();

    Subtask getSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    //выношу список методов для объекта Epic;
    Epic saveEpic(Epic epic);

    ArrayList<Epic> getAllEpics();

    Epic getEpicById(int id);

    void updateEpic(Epic epic);

    //Создаю метод для получения всех подзадач определённого эпика;
    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    //создаю метод для истории просмотров;
    ArrayList<Task> getHistory();

    void deleteEpicById(int id);
}
