package server.handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET" -> {
                    if (path.equals("/subtasks")) {
                        List<Subtask> subtasks = taskManager.getAllSubtasks();
                        sendText(exchange, gson.toJson(subtasks));
                    } else {
                        int id = Integer.parseInt(path.substring("/subtasks/".length()));
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask != null) {
                            sendText(exchange, gson.toJson(subtask));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                }

                case "POST" -> {
                    Subtask subtask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Subtask.class);
                    if (taskManager.checkTaskOverlapWithExisting(subtask)) {
                        sendHasInteractions(exchange);
                    } else {
                        taskManager.saveSubtask(subtask);
                        exchange.sendResponseHeaders(201, -1);
                    }
                }

                case "DELETE" -> {
                    int id = Integer.parseInt(path.substring("/subtasks/".length()));
                    taskManager.deleteSubtaskById(id);
                    exchange.sendResponseHeaders(200, -1);
                }
                default -> sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }
}
