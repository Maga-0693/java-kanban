package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file; //файл для сохранения данных

    public FileBackedTaskManager(File file) {

        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load(); //загрузка данных из файла
        return manager;
    }

    //метод для загрузки данных из файла
    private void load() {
        try {
            String content = Files.readString(file.toPath()); //чтение файла в одну строку
            String[] lines = content.split("\n"); //разбивка на массив строк по символу новой строки

            if (lines.length <= 1) return; // пропускаем заголовок или пустой файл

            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]); //создание задачи из строки
                if (task != null) {
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        subtasks.put(task.getId(), (Subtask) task);

                        //добавляю в эпик id задачи
                        Epic epic = epics.get(((Subtask) task).getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(task.getId());
                        }
                    } else {
                        tasks.put(task.getId(), task);
                    }

                    //обновление счетчика id при необходимости
                    if (task.getId() >= nextId) {
                        nextId = task.getId() + 1;
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
            lines.add("id,type,name,status,description,epic");

            // добавление всех задач
            for (Task task : getAllTasks()) {
                lines.add(toString(task));
            }

            //добавление всех эпиков
            for (Epic epic : getAllEpics()) {
                lines.add(toString(epic));
            }

            //добавление всех подзадач
            for (Subtask subtask : getAllSubtasks()) {
                lines.add(toString(subtask));
            }

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
                    Integer.toString(subtask.getEpicId())
            );
        } else if (task instanceof Epic) {
            return String.join(",",
                    Integer.toString(task.getId()),
                    TaskType.EPIC.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
                    ""
            );
        } else {
            return String.join(",",
                    Integer.toString(task.getId()),
                    TaskType.TASK.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
                    ""
            );
        }
    }

    //метод преобразования строки csv в задачи, эпики и подзадачи по символу запятой
    private Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 6) return null;

        //из массива берем тип, имя, статус и описание задачи
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        //создаем определенный тип задачи
        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(epicId, name, description, status);
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

    //перечисление типов задач
    private enum TaskType {
        TASK,
        EPIC,
        SUBTASK
    }
}


