package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected int nextId = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparingInt(Task::getId)
    );

    @Override
    public boolean isTasksOverlap(Task task1, Task task2) {
        if (task1 == task2) return false;
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    @Override
    public boolean checkTasksOverlap(Task task1, Task task2) {
        return isTasksOverlap(task1, task2);
    }

    @Override
    public boolean checkTaskOverlapWithExisting(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isTasksOverlap(task, existingTask));
    }

    @Override
    public Task saveTask(Task task) {
        if (task.getStartTime() != null && checkTaskOverlapWithExisting(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null && checkTaskOverlapWithExisting(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей задачей");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicDurationAndTime(epic);
            updateEpicStatus(epic);
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic saveEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) return;

        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);

        if (task.getStartTime() != null && checkTaskOverlapWithExisting(task)) {
            prioritizedTasks.add(oldTask);
            throw new ManagerSaveException("Обновленная задача пересекается по времени");
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) return;

        Subtask oldSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(oldSubtask);

        if (subtask.getStartTime() != null && checkTaskOverlapWithExisting(subtask)) {
            prioritizedTasks.add(oldSubtask);
            throw new ManagerSaveException("Обновленная подзадача пересекается по времени");
        }

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicDurationAndTime(epic);
            updateEpicStatus(epic);
        }
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicDurationAndTime(epic);
                updateEpicStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return new ArrayList<>();

        ArrayList<Subtask> result = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) result.add(subtask);
        }
        return result;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected int generateId() {
        return nextId++;
    }

    protected void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            Status status = subtasks.get(subtaskId).getStatus();
            if (status != Status.DONE) allDone = false;
            if (status != Status.NEW) allNew = false;
            if (!allDone && !allNew) break;
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicDurationAndTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpicId(epic.getId());
        if (subtasks.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration duration = Duration.ZERO;
        LocalDateTime start = null;
        LocalDateTime end = null;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                duration = duration.plus(subtask.getDuration());
                if (start == null || subtask.getStartTime().isBefore(start)) {
                    start = subtask.getStartTime();
                }
                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (end == null || subtaskEnd.isAfter(end)) {
                    end = subtaskEnd;
                }
            }
        }

        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    @Override
    public void deleteTasks() {
        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        });
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(subtask.getId());
                updateEpicStatus(epic);
                updateEpicDurationAndTime(epic);
            }
        });
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.values().forEach(epic -> {
            historyManager.remove(epic.getId());
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
                historyManager.remove(subtaskId);
            }
        });
        epics.clear();
    }
}