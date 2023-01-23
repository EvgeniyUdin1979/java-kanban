package api;

import api.errors.ServerTaskIdException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import storetasks.*;
import taskmangers.HttpTaskManager;
import taskmangers.Managers;
import taskmangers.TaskManager;
import taskmangers.erros.ManagerIllegalIdException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpTasksServer {
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .create();
    private static TaskManager manager;
    int SERVER_PORT = 8080;
    HttpServer server;

    public HttpTasksServer(HttpTaskManager httpTaskManager) {
        manager = httpTaskManager;
    }

    public HttpTasksServer() {
        manager = Managers.getDefault();
    }

    public void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
            server.createContext("/tasks", new ServerHandler());
            server.start();
        } catch (NullPointerException | IOException exception) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
    public void stop(){
        server.stop(0);
    }

    static class ServerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String[] splitURLPath = exchange.getRequestURI().getPath().split("/");
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET":
                    toGetMethod(splitURLPath, exchange, requestMethod);
                    break;
                case "POST":
                    if (splitURLPath.length == 3) {
                        if (Integer.parseInt(exchange.getRequestHeaders().get("Content-length").get(0)) > 0) {
                            String typeTask = splitURLPath[2];
                            createTaskFromRequest(exchange, typeTask);
                        } else {
                            String response = GSON.toJson("Ваш запрос не содержит данных для добавления/изменения. " +
                                    "Проверьте и повторите запрос.");
                            sendResponse(exchange, response, 400);
                        }
                    } else {
                        sendBadPathResponse(exchange);
                    }
                    break;
                case "DELETE":
                    if (splitURLPath.length == 3 && splitURLPath[1].equals("tasks")) {
                        String queryRequest = exchange.getRequestURI().getQuery();
                        if (queryRequest != null) {
                            try {
                                int id = getIdFromString(queryRequest.split("=")[1], exchange, requestMethod);
                                String typeTask = splitURLPath[2];
                                switch (typeTask) {
                                    case "task":
                                        manager.deleteNormalTaskById(id);
                                        break;
                                    case "subtask":
                                        manager.deleteSubTaskById(id);
                                        break;
                                    case "epic":
                                        manager.deleteEpicTaskById(id);
                                        break;
                                    default:
                                        sendBadPathResponse(exchange);
                                }
                                sendGoodResponse(exchange);
                                return;
                            } catch (ServerTaskIdException e) {
                                break;
                            }catch (IllegalArgumentException iae){
                               String response = GSON.toJson("В запросе содержится неверный id " + queryRequest.split("=")[1]
                                        + " . Проверьте путь и повторите запрос.");
                                sendResponse(exchange, response, 400);
                            }
                        } else {
                            if (splitURLPath[2].equals("task")) {
                                manager.clearAllTasks();
                                sendGoodResponse(exchange);
                            }
                        }
                    }
                    sendBadPathResponse(exchange);
                    break;
            }
            exchange.close();
        }

        private void sendBadPathResponse(HttpExchange exchange) throws IOException {
            String response = GSON.toJson("В запросе содержится не верный путь." +
                    " Проверьте путь и повторите запрос.");
            sendResponse(exchange, response, 400);
        }

        private void createTaskFromRequest(HttpExchange exchange, String typeTask) throws IOException {
            String response;
            try {
                String body = new String(exchange.getRequestBody().readAllBytes());
                switch (typeTask) {
                    case "task":
                        NormalTask normalTask = GSON.fromJson(body, NormalTask.class);
                        findIllegalFieldsInTasks(normalTask);
                        if (normalTask.getId() > 0) {
                            manager.upgradeNormalTask(normalTask);
                        } else {
                            manager.addNormalTask(normalTask);
                        }
                        sendGoodResponse(exchange);
                        return;
                    case "subtask":
                        SubTask subTask = GSON.fromJson(body, SubTask.class);
                        findIllegalFieldsInTasks(subTask);
                        if (subTask.getId() > 0) {
                            manager.upgradeSubTask(subTask);
                        } else {
                            manager.addSubTask(subTask);
                        }
                        sendGoodResponse(exchange);
                        return;
                    case "epic":
                        EpicTask epicTask = GSON.fromJson(body, EpicTask.class);
                        findIllegalFieldsInTasks(epicTask);
                        if (epicTask.getId() > 0) {
                            manager.upgradeEpicTask(epicTask);
                        } else {
                            manager.addEpicTask(epicTask);
                        }
                        sendGoodResponse(exchange);
                        return;
                    default:
                       sendBadPathResponse(exchange);
                       return;
                }
            } catch (IllegalArgumentException iae) {
                response = GSON.toJson("Ошибка во время десерриализации. Время выполнения задачи пересекается " +
                        "с другой задачей. Проверьте вложенный Json и повторите запрос.");
            } catch (JsonSyntaxException jse) {
                response = GSON.toJson("Ошибка во время десерриализации. " + jse.getMessage() + " Проверьте вложенный Json и повторите запрос.");
            } catch (ManagerIllegalIdException mie) {
                response = GSON.toJson("Ошибка во время десерриализации. Указан не верный id задачи" +
                        ". Проверьте вложенный Json и повторите запрос.");
            }
            sendResponse(exchange, response, 400);
        }

        private void findIllegalFieldsInTasks(Task task) throws JsonSyntaxException {
            if (task.getTitle() == null) {
                task.setTitle("");
            }
            if (task.getDescription() == null) {
                task.setDescription("");
            }
            if (task.getStatus() == null) {
                throw new JsonSyntaxException("Статус не может отсутствовать или быть null");
            } else {
                if (Arrays.stream(StatusTask.values()).noneMatch(statusTask -> statusTask.equals(task.getStatus()))) {
                    throw new JsonSyntaxException("Статус не корректный.");
                }
            }
            if (task.getStartTime() == null) {
                task.setDuration(0);
            }
            if (task instanceof EpicTask) {
                EpicTask epicTask = (EpicTask) task;
                if (epicTask.getSubTasks() == null) {
                    throw new JsonSyntaxException("Лист сабтасов не может быть null.");
                } else {
                    List<SubTask> subTasks = manager.getAllSubTasks();
                    epicTask.getSubTasks().forEach(integer -> {
                        if (subTasks.stream().noneMatch(subTask -> integer == subTask.getId())) {
                            throw new JsonSyntaxException("Epic содержит не корректные id subtask.");
                        }
                        for (SubTask subTask : subTasks) {
                            if (subTask.getId() == integer) {
                                if (subTask.getEpicTaskId() != epicTask.getId()) {
                                    throw new JsonSyntaxException("Эпик содержит id Сабтаски которыя ему не принадлежат");
                                }
                            }
                        }
                    });
                }
            }
            if (task instanceof SubTask) {
                List<EpicTask> epicTasks = manager.getAllEpicTasks();
                SubTask subTask = (SubTask) task;
                if (epicTasks.stream().noneMatch(epicTask -> epicTask.getId() == subTask.getEpicTaskId())) {
                    throw new JsonSyntaxException("Cабтаска содержит id не существующего эпика");
                } else {
                    for (EpicTask epicTask : epicTasks) {
                        if (epicTask.getId() == subTask.getEpicTaskId()) {
                            if (subTask.getId() != 0 && epicTask.getSubTasks().stream().noneMatch(integer -> integer == subTask.getId())) {
                                throw new JsonSyntaxException("Cабтаска содержит id эпика которому не принадлежит.");
                            }
                        }
                    }
                }
            }
        }

        private void sendGoodResponse(HttpExchange exchange) throws IOException {
            String response = GSON.toJson("Запрос обработан.");
            sendResponse(exchange, response, 200);
        }

        private void toGetMethod(String[] splitURL, HttpExchange exchange, String requestMethod) throws IOException,ServerTaskIdException {
            if (splitURL.length == 2 && splitURL[1].equals("tasks")) {
                List<Task> taskList = getAllTasks();
                sendResponse(exchange, GSON.toJson(taskList, ArrayList.class), 200);
            } else if (exchange.getRequestURI().getQuery() == null && splitURL.length == 3) {
                toResponseFromGetTask(splitURL[2], exchange);
            } else if (exchange.getRequestURI().getQuery() != null && splitURL.length == 3) {
                try {
                    int id = getIdFromString(exchange.getRequestURI().getQuery().split("=")[1],
                            exchange, requestMethod);
                    toResponseFromGetTaskById(splitURL[2], id, exchange);
                }catch (ServerTaskIdException ignore){}
            } else if (splitURL.length == 5
                    && splitURL[1].equals("tasks")
                    && splitURL[2].equals("subtask")
                    && splitURL[3].equals("epic")) {
                    int id = getIdFromString(splitURL[4], exchange, requestMethod);
                    String response = GSON.toJson(manager.getByIdEpicTask(id).getSubTasks(), ArrayList.class);
                    sendResponse(exchange, response, 200);
            } else {
               sendBadPathResponse(exchange);
            }
        }

        private List<Task> getAllTasks() {
            return manager.getPrioritizedTasks();
        }

        private int getIdFromString(String taskId, HttpExchange exchange, String method) throws IOException {
            String response;
            try {
                int id = Integer.parseInt(taskId);
                if ((method.equals("GET") || method.equals("DELETE"))
                        && getAllTasks().stream().noneMatch(task -> task.getId() == id)) {
                    throw new IllegalArgumentException();
                }
                return id;
            } catch (NumberFormatException nfe) {
                response = GSON.toJson("В запросе содержится неправельный формат id " + taskId
                        + " . Проверьте путь и повторите запрос.");
                sendResponse(exchange, response, 400);
                throw new ServerTaskIdException();
            } catch (IllegalArgumentException iae) {
                sendBadResponseById(exchange,Integer.parseInt(taskId));
            }
            return 0;
        }

        private void toResponseFromGetTask(String typeTask, HttpExchange exchange) throws IOException {
            String response;
            switch (typeTask) {
                case "task":
                    response = GSON.toJson(manager.getAllNormalTasks(), ArrayList.class);
                    break;
                case "subtask":
                    response = GSON.toJson(manager.getAllSubTasks(), ArrayList.class);
                    break;
                case "epic":
                    response = GSON.toJson(manager.getAllEpicTasks(), ArrayList.class);
                    break;
                case "history":
                    response = GSON.toJson(manager.getHistory(), ArrayList.class);
                    break;
                default:
                    sendBadPathResponse(exchange);
                    return;
            }
            sendResponse(exchange, response, 200);
        }

        private void toResponseFromGetTaskById(String typeTask, int id, HttpExchange exchange) throws IOException,ServerTaskIdException {
            String response;
            switch (typeTask) {
                case "task":
                    if (manager.getAllNormalTasks().stream().noneMatch(normalTask -> normalTask.getId() == id)) {
                        sendBadResponseById(exchange,id);
                        return;
                    }else {
                        response = GSON.toJson(manager.getByIdNormalTask(id), NormalTask.class);
                    }
                    break;
                case "subtask":
                    if (manager.getAllSubTasks().stream().noneMatch(subTask -> subTask.getId() == id)) {
                        sendBadResponseById(exchange,id);
                        return;
                    }else {
                        response = GSON.toJson(manager.getByIdSubTask(id), SubTask.class);
                    }
                    break;
                case "epic":
                    if (manager.getAllEpicTasks().stream().noneMatch(epicTask -> epicTask.getId() == id)) {
                        sendBadResponseById(exchange,id);
                        return;
                    }else {
                        response = GSON.toJson(manager.getByIdEpicTask(id), EpicTask.class);
                    }
                    break;
                default:
                    sendBadPathResponse(exchange);
                    return;
            }
            sendResponse(exchange, response, 200);
        }
        private void sendBadResponseById(HttpExchange exchange, int id) throws IOException,ServerTaskIdException {
            String response = GSON.toJson("В запросе содержится неверный id " + id
                    + " . Проверьте путь и повторите запрос.");
            sendResponse(exchange, response, 400);
            throw new ServerTaskIdException();
        }

        public void sendResponse(HttpExchange exchange, String response, int code) throws IOException {
            byte[] body = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Accept", "application/json");
            exchange.sendResponseHeaders(code, body.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(body);
            outputStream.close();
        }
    }
}
