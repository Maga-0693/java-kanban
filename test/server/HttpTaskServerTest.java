package server;
import manager.TaskManager;
import manager.Managers;
import model.Task;
import model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private HttpTaskServer taskServer;
    private TaskManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        HttpClient client = HttpClient.newHttpClient();
        String taskJson = "{\"name\":\"Test Task\",\"description\":\"Description\",\"status\":\"NEW\",\"duration\":30,\"startTime\":\"" + LocalDateTime.now() + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем, что статус ответа 201 (Created)
        assertEquals(201, response.statusCode());
    }
}

