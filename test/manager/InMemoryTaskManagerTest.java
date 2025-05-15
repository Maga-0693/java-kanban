package manager;

import model.Task;
import model.Status;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {
    @Test
    public void testSaveAndGetTask() {
        TaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Тестирование задачи", "Описание", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        manager.saveTask(task);

        Task savedTask = manager.getTaskById(task.getId());
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(task.getStatus(), savedTask.getStatus());
    }
}
