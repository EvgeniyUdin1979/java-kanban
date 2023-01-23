import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.StatusTask;
import storetasks.SubTask;
import taskmangers.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HttpTasksServerTest extends HttpTaskManagerTest {
    enum METHOD {GET, POST, DELETE}

    Gson gson = Managers.getDefaultGson();

    private HttpResponse<String> createClient(String path, METHOD method, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI("http://localhost:8080/" + path);
            HttpRequest request;
            switch (method) {
                case GET:
                    request = HttpRequest.newBuilder(uri).GET().build();
                    break;
                case POST:
                    request = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(json)).build();
                    break;
                case DELETE:
                    request = HttpRequest.newBuilder(uri).DELETE().build();
                    break;
                default:
                    throw new IOException();
            }
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void methodGET() {
        HttpResponse<String> responseAllTasks = createClient("tasks", METHOD.GET, null);
        String all = "[{\"id\":1,\"title\":\"firstNormal\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":6,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"subTasks\":[3,4,5],\"id\":2,\"title\":\"firstEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":28,\"nano\":903000000}},\"duration\":30},{\"epicTaskId\":2,\"id\":3,\"title\":\"firstSub1\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":4,\"title\":\"firstSub2\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":36,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":6,\"id\":7,\"title\":\"firstSub4\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0},{\"subTasks\":[7],\"id\":6,\"title\":\"secondEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0},{\"epicTaskId\":2,\"id\":5,\"title\":\"firstSub3\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0}]";
        assertEquals(all, responseAllTasks.body(), "Запрос возврацает не правильный результат");

        HttpResponse<String> responseAllNormal = createClient("tasks/task", METHOD.GET, null);
        String allNormal = "[{\"id\":1,\"title\":\"firstNormal\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":6,\"second\":29,\"nano\":903000000}},\"duration\":15}]";
        assertEquals(allNormal, responseAllNormal.body(), "Запрос возврацает не правильный результат");

        HttpResponse<String> responseAllSub = createClient("tasks/subtask", METHOD.GET, null);
        String allSub = "[{\"epicTaskId\":2,\"id\":3,\"title\":\"firstSub1\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":4,\"title\":\"firstSub2\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":36,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":5,\"title\":\"firstSub3\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0},{\"epicTaskId\":6,\"id\":7,\"title\":\"firstSub4\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0}]";
        assertEquals(allSub, responseAllSub.body(), "Запрос возврацает не правильный результат");

        HttpResponse<String> responseAllEpic = createClient("tasks/epic", METHOD.GET, null);
        String allEpic = "[{\"subTasks\":[3,4,5],\"id\":2,\"title\":\"firstEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":28,\"nano\":903000000}},\"duration\":30},{\"subTasks\":[7],\"id\":6,\"title\":\"secondEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0}]";
        assertEquals(allEpic, responseAllEpic.body(), "Запрос возврацает не правильный результат");

        HttpResponse<String> responseNormal1 = createClient("tasks/task?id=1", METHOD.GET, null);
        String getNormal1 = "{\"id\":1,\"title\":\"firstNormal\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":6,\"second\":29,\"nano\":903000000}},\"duration\":15}";
        assertEquals(getNormal1, responseNormal1.body(), "Запрос возврацает не правильный результат");

        HttpResponse<String> responseSub3 = createClient("tasks/subtask?id=3", METHOD.GET, null);
        String getSub3 = "{\"epicTaskId\":2,\"id\":3,\"title\":\"firstSub1\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":29,\"nano\":903000000}},\"duration\":15}";
        assertEquals(getSub3, responseSub3.body(), "Запрос возврацает не правильный результат");

        HttpResponse<String> responseEpic2 = createClient("tasks/epic?id=2", METHOD.GET, null);
        String getEpic2 = "{\"subTasks\":[3,4,5],\"id\":2,\"title\":\"firstEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":28,\"nano\":903000000}},\"duration\":30}";
        assertEquals(getEpic2, responseEpic2.body(), "Запрос возврацает не правильный результат");

        HttpResponse<String> responseHistory = createClient("tasks/", METHOD.GET, null);
        String getHistory = "[{\"id\":1,\"title\":\"firstNormal\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":6,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"subTasks\":[3,4,5],\"id\":2,\"title\":\"firstEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":28,\"nano\":903000000}},\"duration\":30},{\"epicTaskId\":2,\"id\":3,\"title\":\"firstSub1\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":4,\"title\":\"firstSub2\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":36,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":6,\"id\":7,\"title\":\"firstSub4\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0},{\"subTasks\":[7],\"id\":6,\"title\":\"secondEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0},{\"epicTaskId\":2,\"id\":5,\"title\":\"firstSub3\",\"description\":\"\",\"status\":\"New\",\"startTime\":null,\"duration\":0}]";
        assertEquals(getHistory, responseHistory.body(), "Запрос возврацает не правильный результат");
    }

    @Test
    public void methodGetNormalErrors() {
        HttpResponse<String> responseBadEndPoint = createClient("task", METHOD.GET, null);
        assertEquals(404, responseBadEndPoint.statusCode(), "Должена быть реакция на не правильный эндпоинт!");
        assertEquals("<h1>404 Not Found</h1>No context found for request", responseBadEndPoint.body(), "Сообщение неправильное");

        HttpResponse<String> responseBadPathNormal = createClient("tasks/task/a", METHOD.GET, null);
        assertEquals(400, responseBadPathNormal.statusCode(), "Должена быть реакция на не правильный путь!");
        assertEquals("\"В запросе содержится не верный путь. Проверьте путь и повторите запрос.\"", responseBadPathNormal.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadPathNormal2 = createClient("tasks/a", METHOD.GET, null);
        assertEquals(400, responseBadPathNormal2.statusCode(), "Должена быть реакция на не правильный путь!");
        assertEquals("\"В запросе содержится не верный путь. Проверьте путь и повторите запрос.\"", responseBadPathNormal2.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadIdNormal = createClient("tasks/task?id=2", METHOD.GET, null);
        assertEquals(400, responseBadIdNormal.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неверный id 2 . Проверьте путь и повторите запрос.\"", responseBadIdNormal.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadIdNormalString = createClient("tasks/task?id=a", METHOD.GET, null);
        assertEquals(400, responseBadIdNormalString.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неправельный формат id a . Проверьте путь и повторите запрос.\"", responseBadIdNormalString.body(), "Сообщение неправильное");
    }

    @Test
    public void methodGetSubErrors() {
        HttpResponse<String> responseBadPathSub = createClient("tasks/subtask/a", METHOD.GET, null);
        assertEquals(400, responseBadPathSub.statusCode(), "Должена быть реакция на не правильный путь!");
        assertEquals("\"В запросе содержится не верный путь. Проверьте путь и повторите запрос.\"", responseBadPathSub.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadIdSub = createClient("tasks/subtask?id=2", METHOD.GET, null);
        assertEquals(400, responseBadIdSub.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неверный id 2 . Проверьте путь и повторите запрос.\"", responseBadIdSub.body(), "Сообщение неправильное");

        HttpResponse<String> responseBadIdSubString = createClient("tasks/subtask?id=a", METHOD.GET, null);
        assertEquals(400, responseBadIdSubString.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неправельный формат id a . Проверьте путь и повторите запрос.\"", responseBadIdSubString.body(), "Сообщение неправильное");

    }

    @Test
    public void methodGetEpicErrors() {
        HttpResponse<String> responseBadPathEpic = createClient("tasks/epic/a", METHOD.GET, null);
        assertEquals(400, responseBadPathEpic.statusCode(), "Должена быть реакция на не правильный путь!");

        HttpResponse<String> responseBadIdEpic = createClient("tasks/epic?id=1", METHOD.GET, null);
        assertEquals(400, responseBadIdEpic.statusCode(), "Должена быть реакция на не правильный id!");

        HttpResponse<String> responseBadIdEpicString = createClient("tasks/epic?id=a", METHOD.GET, null);
        assertEquals(400, responseBadIdEpicString.statusCode(), "Должена быть реакция на не правильный id!");
    }

    @Test
    public void methodPostIdZero() {
        NormalTask normalTask = new NormalTask("firstSub1", "HttpServer", StatusTask.New, null, 0);
        HttpResponse<String> responseNormal = createClient("tasks/task", METHOD.POST,
                gson.toJson(normalTask, NormalTask.class));
        normalTask.setId(8);
        assertEquals(200, responseNormal.statusCode(), "Код ответа не верный");
        assertEquals(httpTaskManager.getByIdNormalTask(8), normalTask, "Десериализация прошла неправильно!");

        EpicTask epic = new EpicTask("firstEpic1", "HttpServer", StatusTask.New);
        HttpResponse<String> responseEpic = createClient("tasks/epic", METHOD.POST,
                gson.toJson(epic, EpicTask.class));
        epic.setId(9);
        assertEquals(200, responseEpic.statusCode(), "Код ответа не верный");
        assertEquals(httpTaskManager.getByIdEpicTask(9), epic, "Десериализация прошла неправильно!");

        SubTask subTask = new SubTask("firstSub1", "HttpServer", StatusTask.New, LocalDateTime.now(startTestTime).plusHours(2), 30, 9);
        HttpResponse<String> responseSub = createClient("tasks/subtask", METHOD.POST,
                gson.toJson(subTask, SubTask.class));
        subTask.setId(10);
        assertEquals(200, responseSub.statusCode(), "Код ответа не верный");
        assertEquals(httpTaskManager.getByIdSubTask(10), subTask, "Десериализация прошла неправильно!");
    }

    @Test
    public void methodPostIdNotZero() {
        NormalTask normalTask = new NormalTask("firstSub1", "HttpServer", StatusTask.In_progress, null, 0);
        normalTask.setId(1);
        HttpResponse<String> responseNormal = createClient("tasks/task", METHOD.POST,
                gson.toJson(normalTask, NormalTask.class));
        assertEquals(200, responseNormal.statusCode(), "Код ответа не верный");
        assertEquals(httpTaskManager.getByIdNormalTask(1), normalTask, "Десериализация прошла неправильно!");

        EpicTask epic = manager.getByIdEpicTask(2);
        epic.setDescription("HttpServer");
        HttpResponse<String> responseEpic = createClient("tasks/epic", METHOD.POST,
                gson.toJson(epic, EpicTask.class));
        assertEquals(200, responseEpic.statusCode(), "Код ответа не верный");
        assertEquals(httpTaskManager.getByIdEpicTask(2), epic, "Десериализация прошла неправильно!");

        SubTask subTask = manager.getByIdSubTask(3);
        subTask.setStartTime(LocalDateTime.from(subTask.getStartTime().plusHours(2)));
        subTask.setDescription("HttpServer");
        HttpResponse<String> responseSub = createClient("tasks/subtask", METHOD.POST,
                gson.toJson(subTask, SubTask.class));
        assertEquals(200, responseSub.statusCode(), "Код ответа не верный");
        assertEquals(httpTaskManager.getByIdSubTask(3), subTask, "Десериализация прошла неправильно!");
    }

    @Test
    public void methodPostErrors() {
        String ErrorIdInJson = "{\"id\":2,\"title\":\"firstNormal\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":6,\"second\":29,\"nano\":903000000}},\"duration\":15}";
        HttpResponse<String> responseErrorJson = createClient("tasks/task", METHOD.POST,
                ErrorIdInJson);
        assertEquals(400, responseErrorJson.statusCode(), "Код ответа не верный");
        assertEquals("Ошибка во время десерриализации. Указан не верный id задачи. Проверьте вложенный Json и повторите запрос.",
                gson.fromJson(responseErrorJson.body(), String.class), "Не правильный текст ответа!");

        String ErrorEmptyJson = "{}";
        HttpResponse<String> responseErrorEmptyJson = createClient("tasks/task", METHOD.POST,
                ErrorEmptyJson);
        assertEquals(400, responseErrorEmptyJson.statusCode(), "Код ответа не верный");
        assertEquals("Ошибка во время десерриализации. Статус не может отсутствовать или быть null Проверьте вложенный Json и повторите запрос.",
                gson.fromJson(responseErrorEmptyJson.body(), String.class), "Не правильный текст ответа!");

        String ErrorNoJson = "";
        HttpResponse<String> responseErrorNoJson = createClient("tasks/task", METHOD.POST,
                ErrorNoJson);
        assertEquals(400, responseErrorNoJson.statusCode(), "Код ответа не верный");
        assertEquals("Ваш запрос не содержит данных для добавления/изменения. Проверьте и повторите запрос.",
                gson.fromJson(responseErrorNoJson.body(), String.class), "Не правильный текст ответа!");

        String ErrorSubIntersectionOfTime = "{\"epicTaskId\":2,\"id\":3,\"title\":\"firstSub1\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":29,\"nano\":903000000}},\"duration\":30}";
        SubTask subTaskAfterRequest = manager.getByIdSubTask(3);
        HttpResponse<String> responseErrorSubIntersectionOfTime = createClient("tasks/subtask", METHOD.POST,
                ErrorSubIntersectionOfTime);
        System.out.println(responseErrorSubIntersectionOfTime.body());
        assertEquals(400, responseErrorSubIntersectionOfTime.statusCode(), "Код ответа не верный");
        assertEquals(subTaskAfterRequest, manager.getByIdSubTask(3), "Задача изменилась после ошибочного запроса!");
        assertEquals("Ошибка во время десерриализации. Время выполнения задачи пересекается с другой задачей. Проверьте вложенный Json и повторите запрос.",
                gson.fromJson(responseErrorSubIntersectionOfTime.body(), String.class), "Не правильный текст ответа!");
    }

    @Test
    public void methodDeleteAll() {
        HttpResponse<String> responseDelete = createClient("tasks/task", METHOD.DELETE,
                null);
        System.out.println(responseDelete.body());
        assertEquals(200, responseDelete.statusCode(), "Код ответа не верный");
        assertEquals(0,
                httpTaskManager.getAllEpicTasks().size() +
                        httpTaskManager.getAllSubTasks().size() +
                        httpTaskManager.getAllNormalTasks().size() +
                        httpTaskManager.getHistory().size() +
                        httpTaskManager.getPrioritizedTasks().size()
                , "Не все было удалено!");
    }

    @Test
    public void methodDelete() {
        NormalTask normalTaskForDelete = manager.getByIdNormalTask(1);
        HttpResponse<String> responseDeleteNormal = createClient("tasks/task?id=1", METHOD.DELETE,null);
        assertEquals(200, responseDeleteNormal.statusCode(), "Код ответа не верный");
        assertFalse(manager.getAllNormalTasks().contains(normalTaskForDelete), "Таск id 1 небыл удален!");

        SubTask subTaskForDelete = manager.getByIdSubTask(3);
        HttpResponse<String> responseDeleteSub = createClient("tasks/subtask?id=3", METHOD.DELETE,null);
        assertEquals(200, responseDeleteSub.statusCode(), "Код ответа не верный");
        assertFalse(manager.getAllSubTasks().contains(subTaskForDelete), "Таск id 3 небыл удален!");

        EpicTask epicTaskForDelete = manager.getByIdEpicTask(2);
        HttpResponse<String> responseDeleteEpic = createClient("tasks/epic?id=2", METHOD.DELETE,null);
        assertEquals(200, responseDeleteEpic.statusCode(), "Код ответа не верный");
        assertFalse(manager.getAllEpicTasks().contains(epicTaskForDelete), "Таск id 2 небыл удален!");
    }

    @Test
    public void methodDeleteNormalErrors() {
        HttpResponse<String> responseBadEndPoint = createClient("task", METHOD.DELETE, null);
        assertEquals(404, responseBadEndPoint.statusCode(), "Должена быть реакция на не правильный эндпоинт!");
        assertEquals("<h1>404 Not Found</h1>No context found for request", responseBadEndPoint.body(), "Сообщение неправильное");

        HttpResponse<String> responseBadPathNormal = createClient("tasks/task/a", METHOD.DELETE, null);
        assertEquals(400, responseBadPathNormal.statusCode(), "Должена быть реакция на не правильный путь!");
        assertEquals("\"В запросе содержится не верный путь. Проверьте путь и повторите запрос.\"", responseBadPathNormal.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadPathNormal2 = createClient("tasks/a", METHOD.DELETE, null);
        assertEquals(400, responseBadPathNormal2.statusCode(), "Должена быть реакция на не правильный путь!");
        assertEquals("\"В запросе содержится не верный путь. Проверьте путь и повторите запрос.\"", responseBadPathNormal2.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadIdNormal = createClient("tasks/subtask?id=2", METHOD.DELETE, null);
        assertEquals(400, responseBadIdNormal.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неверный id 2 . Проверьте путь и повторите запрос.\"", responseBadIdNormal.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadIdNormalString = createClient("tasks/task?id=a", METHOD.DELETE, null);
        assertEquals(400, responseBadIdNormalString.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неправельный формат id a . Проверьте путь и повторите запрос.\"", responseBadIdNormalString.body(), "Сообщение неправильное");
    }

    @Test
    public void methodDeleteSubErrors() {
        HttpResponse<String> responseBadPathSub = createClient("tasks/subtask/a", METHOD.DELETE, null);
        assertEquals(400, responseBadPathSub.statusCode(), "Должена быть реакция на не правильный путь!");
        assertEquals("\"В запросе содержится не верный путь. Проверьте путь и повторите запрос.\"", responseBadPathSub.body(), "Сообщение неправильное");


        HttpResponse<String> responseBadIdSub = createClient("tasks/subtask?id=2", METHOD.DELETE, null);
        assertEquals(400, responseBadIdSub.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неверный id 2 . Проверьте путь и повторите запрос.\"", responseBadIdSub.body(), "Сообщение неправильное");

        HttpResponse<String> responseBadIdSubString = createClient("tasks/subtask?id=a", METHOD.DELETE, null);
        assertEquals(400, responseBadIdSubString.statusCode(), "Должена быть реакция на не правильный id!");
        assertEquals("\"В запросе содержится неправельный формат id a . Проверьте путь и повторите запрос.\"", responseBadIdSubString.body(), "Сообщение неправильное");

    }

    @Test
    public void methodDeleteEpicErrors() {
        HttpResponse<String> responseBadPathEpic = createClient("tasks/epic/a", METHOD.DELETE, null);
        assertEquals(400, responseBadPathEpic.statusCode(), "Должена быть реакция на не правильный путь!");

        HttpResponse<String> responseBadIdEpic = createClient("tasks/epic?id=1", METHOD.DELETE, null);
        assertEquals(400, responseBadIdEpic.statusCode(), "Должена быть реакция на не правильный id!");

        HttpResponse<String> responseBadIdEpicString = createClient("tasks/epic?id=a", METHOD.DELETE, null);
        assertEquals(400, responseBadIdEpicString.statusCode(), "Должена быть реакция на не правильный id!");
    }


}




