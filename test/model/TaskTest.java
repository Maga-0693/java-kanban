package model;

import model.Task;
import model.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    @Test
    public void testTaskCreation() {
        Task task = new Task("Test Task", "Description", Status.NEW);
        assertEquals("Test Task", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }
}