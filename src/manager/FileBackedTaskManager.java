package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file; //файл для сохранения данных

    public FileBackedTaskManager(File file) {

        this.file = file;
        load(); //загружаю данные при создании менеджера
    }

    //метод для загрузки данных из файла
    private void load() {
        try {
            if (!file.exists()) return;

            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) return;

            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                if (task != null) {
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        subtasks.put(task.getId(), (Subtask) task);
                        Epic epic = epics.get(((Subtask) task).getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(task.getId()); //связываю подзадачи с эпиком
                        }
                    } else {
                        tasks.put(task.getId(), task);
                    }
                    prioritizedTasks.add(task); //добавляю в приоритетный список
                    if (task.getId() >= nextId) {
                        nextId = task.getId() + 1; //обновляю счетчика ID
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
    }

    //метод для сохранения данных в файл
    private void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,epic,duration,startTime");

            getAllTasks().forEach(task -> lines.add(toString(task)));
            getAllEpics().forEach(epic -> lines.add(toString(epic)));
            getAllSubtasks().forEach(subtask -> lines.add(toString(subtask)));

            //список строк записывается в файл, при исключении выдает ошибку
            Files.write(file.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    //метод преобразования подзадач, эпиков и задач в строку csv
    private String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.join(",",
                    Integer.toString(subtask.getId()),
                    TaskType.SUBTASK.name(),
                    subtask.getName(),
                    subtask.getStatus().name(),
                    subtask.getDescription(),
                    Integer.toString(subtask.getEpicId()),
                    Long.toString(subtask.getDuration().toMinutes()),
                    subtask.getStartTime().toString()
            );
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            // Добавляем проверку на null для duration
            String durationStr = epic.getDuration() != null ?
                    Long.toString(epic.getDuration().toMinutes()) : "0";
            String startTimeStr = epic.getStartTime() != null ?
                    epic.getStartTime().toString() : "";

            return String.join(",",
                    Integer.toString(epic.getId()),
                    TaskType.EPIC.name(),
                    epic.getName(),
                    epic.getStatus().name(),
                    epic.getDescription(),
                    "",
                    durationStr,
                    startTimeStr
            );
        } else {
            Task regularTask = task;
            return String.join(",",
                    Integer.toString(regularTask.getId()),
                    TaskType.TASK.name(),
                    regularTask.getName(),
                    regularTask.getStatus().name(),
                    regularTask.getDescription(),
                    "",
                    Long.toString(regularTask.getDuration().toMinutes()),
                    regularTask.getStartTime().toString()
            );
        }
    }

    //метод преобразования строки csv в задачи, эпики и подзадачи по символу запятой
    private Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 8) return null;

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        // Обработка duration (может быть пустым для эпиков)
        Duration duration = parts[6].isEmpty() ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(parts[6]));

        // Обработка startTime (может быть пустым)
        LocalDateTime startTime = parts[7].isEmpty() ? null : LocalDateTime.parse(parts[7]);

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(epicId, name, description, status, duration, startTime);
                subtask.setId(id);
                return subtask;
            default:
                return null;
        }
    }

    //переопределяем методы для сохранения, обновления и удаления задач
    @Override
    public Task saveTask(Task task) {
        Task savedTask = super.saveTask(task);
        save();
        return savedTask;
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        Subtask savedSubtask = super.saveSubtask(subtask);
        save();
        return savedSubtask;
    }

    @Override
    public Epic saveEpic(Epic epic) {
        Epic savedEpic = super.saveEpic(epic);
        save();
        return savedEpic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    //загрузка менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {

        return new FileBackedTaskManager(file);
    }

    //перечисление типов задач
    private enum TaskType {
        TASK,
        EPIC,
        SUBTASK
    }
}
