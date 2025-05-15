package server.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    // обработка GET-запросов
                    break;
                case "POST":
                    if ("/tasks".equals(path)) {
                        Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Task.class);
                        if (taskManager.checkTaskOverlapWithExisting(task)) {
                            sendHasInteractions(exchange);
                        } else {
                            taskManager.saveTask(task);
                            exchange.sendResponseHeaders(201, -1); // Устанавливаем код ответа 201
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "DELETE":
                    // обработка DELETE-запросов
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }
}

