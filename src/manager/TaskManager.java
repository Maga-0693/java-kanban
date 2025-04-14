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
    void deleteEpicById(int id);
    
    ArrayList<Subtask> getSubtasksByEpicId(int epicId);//Создаю метод для получения всех подзадач определённого эпика;

    ArrayList<Task> getHistory();//создаю метод для истории просмотров;
}
