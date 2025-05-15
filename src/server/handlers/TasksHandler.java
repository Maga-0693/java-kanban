package server.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

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
                    if (path.equals("/tasks")) {
                        List<Task> tasks = taskManager.getAllTasks();
                        sendText(exchange, gson.toJson(tasks));
                    } else {
                        int id = Integer.parseInt(path.substring("/tasks/".length()));
                        Task task = taskManager.getTaskById(id);
                        if (task != null) {
                            sendText(exchange, gson.toJson(task));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Task.class);
                    if (taskManager.checkTaskOverlapWithExisting(task)) {
                        sendHasInteractions(exchange);
                    } else {
                        taskManager.saveTask(task);
                        exchange.sendResponseHeaders(201, -1);
                    }
                    break;
                case "DELETE":
                    int id = Integer.parseInt(path.substring("/tasks/".length()));
                    taskManager.deleteTaskById(id);
                    exchange.sendResponseHeaders(200, -1);
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
