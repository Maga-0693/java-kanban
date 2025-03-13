package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    //Хранилище для задач,подзадач,эпиков. Ключ — ID задачи,подзадач,эпиков; значение — самих задач,подзадач,эпиков.
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();// Метод для получения всех подзадач определённого эпика
    //ArrayList<Subtask> getSubtasksByEpicId(int epicId);/*!удален отсюда, т.к. перенесен в интерфейс TaskManager!*/
    //Счетчик для генерации ID
    private int nextId = 1;

    //создаем список для хранения истории просмотров
    //заменяем этот список: private final ArrayList<Task> history = new ArrayList<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //генерация ID для новой задачи, подзадачи или эпика
    private int generateId() {
        return nextId++;
    }

    //Сохранение задачи в хранилище
    @Override
    public Task saveTask(Task task) {
        task.setId(generateId()); // Присваиваем задаче уникальный ID
        tasks.put(task.getId(), task); //Добавляем задачу в хранилище
        return task;
    }

    //Сохранение подзадачи в хранилище
    @Override
    public Subtask saveSubtask(Subtask subtask) {
        subtask.setId(generateId()); //Присваиваем подзадаче ID
        subtasks.put(subtask.getId(), subtask); //Добавляем подзадачу в хранилище
        Epic epic = epics.get(subtask.getEpicId()); // Находим эпик, к которому относится подзадача
        if (epic != null) {
            epic.addSubtaskId(subtask.getId()); //Добавляем ID подзадачи в список подзадач эпик
            updateEpicStatus(epic); //Обновляю статус эпика
        }
        return subtask;
    }

    //Сохранение эпика в хранилище
    @Override
    public Epic saveEpic(Epic epic) {
        epic.setId(generateId()); //Присваиваем эпику ID
        epics.put(epic.getId(), epic); //Добавляем эпик в хранилище
        return epic;
    }

    @Override
    public ArrayList<Task> getAllTasks() { //Получение списка всех задач
        return new ArrayList<>(tasks.values()); //Возвращаем список всех задач
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    //Получение задачи по ID
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task); // Добавляем задачу в историю
        }
        return task; //Возвращаем задачу
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask); // Добавляем подзадачу в историю
        }
        return subtask; //возвращаем подзадачу
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic); // Добавляем эпик в историю
        }
        return epic; //возвращаем эпик
    }

    //Обновление задачи
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) { //Проверяем, существует ли задача
            tasks.put(task.getId(), task); //Обновляем задачу в хранилище
        }
    }

    //Обновление подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) { //Проверяем, существует ли подзадача
            subtasks.put(subtask.getId(), subtask); //Обновляем подзадачу в хранилище
            Epic epic = epics.get(subtask.getEpicId()); //Находим эпик, к которому относится подзадача
            if (epic != null) {
                updateEpicStatus(epic); //Проверка на null и обновляем статус эпика
            }
        }
    }

    //Обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    //Удаление задачи по ID
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id); //Удаляем задачу из хранилища
    }

    //Удаление подзадачи по ID
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id); //Удаляем подзадачу из хранилища
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId()); //Находим эпик, к которому относится подзадача
            if (epic != null) { //Проверка на null
                epic.removeSubtaskId(id); //Удаляем ID подзадачи из списка подзадач эпика
                updateEpicStatus(epic); //Обновляем статус эпика
            }
        }
    }

    //Удаляем эпик по ID
    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id); //Удаляем эпик из хранилища
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) { //Удаляем все подзадачи, связанные с этим эпиком
                subtasks.remove(subtaskId);
            }
        }
    }

    //Получение списка подзадач по ID эпика
    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>(); //Создаем список для результата
        Epic epic = epics.get(epicId); //Находим эпик по его ID
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) { //Перебираем ID подзадач эпика
                Subtask subtask = subtasks.get(subtaskId); //Находим подзадачу по её ID
                if (subtask != null) {
                    result.add(subtask); //Добавляем подзадачу в результат
                }
            }
        }
        return result; //Возвращаем список подзадач
    }
    @Override
    public ArrayList<Task> getHistory() {
        // заменяем: return new ArrayList<>(history) с использованием интерфейса HistoryManager; // Возвращаем копию списка истории
        return historyManager.getHistory();
    }
//    //создаю метод для добавления задач в историю
      //переносим данный метод в класс InMemoryHistoryManager
//    private void addToHistory(Task task) {
//        if (history.size() == 10) { // если размер история равен 10, то
//            history.remove(0); // удаляем самый старый элемент
//        }
//        history.add(task); // иначе добавлем новый элемент
//    }

    //Обновление статуса эпика на основе статусов его подзадач
    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) { //Если у эпика нет подзадач, статус NEW
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true; //Предполагаем, что все подзадачи выполнены
        boolean allNew = true; //Предполагаем, что все подзадачи новые

        for (int subtaskId : epic.getSubtaskIds()) { //Перебираем подзадачи эпика
            Subtask subtask = subtasks.get(subtaskId); //Находим подзадачу по её ID
            if (subtask != null) {
                if (subtask.getStatus() != Status.DONE) { //Если подзадача не выполнена
                    allDone = false;
                }
                if (subtask.getStatus() != Status.NEW) { //Если подзадача не новая
                    allNew = false;
                }
            }
        }

        if (allDone) { //Если все подзадачи выполнены
            epic.setStatus(Status.DONE);
        } else if (allNew) { //Если все подзадачи новые
            epic.setStatus(Status.NEW);
        } else { //В остальных случаях статус IN_PROGRESS
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
