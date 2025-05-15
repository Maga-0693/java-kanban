package manager;

import manager.FileBackedTaskManager;
import manager.TaskManager;
import model.Task;
import model.Status;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    @Test
    public void testSaveAndLoadTask() throws IOException {
        File file = new File("test_tasks.csv");
        TaskManager manager = FileBackedTaskManager.loadFromFile(file);

        Task task = new Task("Test Task", "Description", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        manager.saveTask(task);

        Task loadedTask = manager.getTaskById(task.getId());
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
    }
}
