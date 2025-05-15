package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
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
                    if (path.equals("/epics")) {
                        List<Epic> epics = taskManager.getAllEpics();
                        sendText(exchange, gson.toJson(epics));
                    } else {
                        int id = Integer.parseInt(path.substring("/epics/".length()));
                        Epic epic = taskManager.getEpicById(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    Epic epic = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Epic.class);
                    taskManager.saveEpic(epic);
                    exchange.sendResponseHeaders(201, -1);
                    break;
                case "DELETE":
                    int id = Integer.parseInt(path.substring("/epics/".length()));
                    taskManager.deleteEpicById(id);
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
