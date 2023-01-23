import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storetasks.Task;
import taskmangers.HttpTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

class HttpTaskManagerTest extends TaskManagerTest {

    HttpTaskManager httpTaskManager = new HttpTaskManager("localhost");

    public HttpTaskManagerTest() {
        httpTaskManager.startServers();
        super.setManager(httpTaskManager);
    }

    @BeforeEach
    public void deleteCSV() {
        try {
            Files.deleteIfExists(Path.of("history.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> createClient(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI("http://localhost:8078/load/" + key + "?API_TOKEN=DEBUG");
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeserialization(){
        HttpTaskManager testHttpTaskManager = new HttpTaskManager("localhost");
        testHttpTaskManager.createKVClient();
        testHttpTaskManager.load();

        Assertions.assertEquals(7,testHttpTaskManager.getPrioritizedTasks().size(),
                "Колличество тасков отличается после десериализации");
        Assertions.assertEquals(List.of(2,6), testHttpTaskManager.getHistory().stream().map(Task::getId).collect(Collectors.toList()),
                "История содерфит не правильные таски после десериализации");
    }

    @AfterEach
    public void stop() {
        httpTaskManager.stopServers();
    }

    @Test
    @Override
    void testClearAllTasks() {
        super.testClearAllTasks();
        Assertions.assertEquals("[]",createClient("task").body());
        Assertions.assertEquals("[]",createClient("subtask").body());
        Assertions.assertEquals("[]",createClient("epic").body());
        Assertions.assertEquals("[]",createClient("history").body());
    }

    @Test
    @Override
    void testDeleteNormalTaskById() {
        super.testDeleteNormalTaskById();
        Assertions.assertEquals("[]",createClient("task").body(),
                "Удаление Нормала не правильно отражено на сервере!");

    }

    @Test
    @Override
    void testDeleteEpicTaskById() {
        super.testDeleteEpicTaskById();
        Assertions.assertEquals("[{\"subTasks\":[7],\"id\":6,\"title\":\"secondEpic\",\"description\":\"\",\"status\":\"New\",\"duration\":0}]",
                createClient("epic").body(),
                "Удаление Эпика не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testDeleteSubTaskById() {
        super.testDeleteSubTaskById();
        Assertions.assertEquals("[{\"epicTaskId\":2,\"id\":4,\"title\":\"firstSub2\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":36,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":5,\"title\":\"firstSub3\",\"description\":\"\",\"status\":\"New\",\"duration\":0},{\"epicTaskId\":6,\"id\":7,\"title\":\"firstSub4\",\"description\":\"\",\"status\":\"New\",\"duration\":0}]",
                createClient("subtask").body(),
                "Удаление Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testGetByIdNormalTask() {
        super.testGetByIdNormalTask();
        Assertions.assertEquals("[2,6,1]",createClient("history").body(),
                "Получение Нормала не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testGetByIdEpicTask() {
        super.testGetByIdEpicTask();
        Assertions.assertEquals("[6,2]",createClient("history").body(),
                "Получение Эпика не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testGetByIdSubTask() {
        super.testGetByIdSubTask();
        Assertions.assertEquals("[2,6,3]",createClient("history").body(),
                "Получение Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testAddNormalTask() {
        super.testAddNormalTask();
        Assertions.assertEquals("[{\"id\":1,\"title\":\"firstNormal\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":6,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"id\":8,\"title\":\"normalTest\",\"description\":\"test test\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":20,\"minute\":6,\"second\":29,\"nano\":903000000}},\"duration\":20}]",
                createClient("task").body(),
                "Добавление Нормала не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testAddEpicTask() {
        super.testAddEpicTask();

        Assertions.assertEquals("[{\"subTasks\":[3,4,5],\"id\":2,\"title\":\"firstEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":28,\"nano\":903000000}},\"duration\":30},{\"subTasks\":[7],\"id\":6,\"title\":\"secondEpic\",\"description\":\"\",\"status\":\"New\",\"duration\":0},{\"subTasks\":[],\"id\":8,\"title\":\"epicTest\",\"description\":\"test test\",\"status\":\"New\",\"duration\":0}]",
                createClient("epic").body(),
                "Добавление Эпика не правильно отражено на сервере!");
    }
    @Test
    @Override
    void testChangeStatusEpicTask() {
        super.testChangeStatusEpicTask();
        Assertions.assertEquals("[{\"subTasks\":[3,4,5],\"id\":2,\"title\":\"firstEpic\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":28,\"nano\":903000000}},\"duration\":30},{\"subTasks\":[7],\"id\":6,\"title\":\"secondEpic\",\"description\":\"\",\"status\":\"New\",\"duration\":0},{\"subTasks\":[9,10,11],\"id\":8,\"title\":\"epicTest\",\"description\":\"test test\",\"status\":\"In_progress\",\"duration\":0}]",
                createClient("epic").body(),
                "Изменение статуса Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testAddSubTask() {
        super.testAddSubTask();
        Assertions.assertEquals("[{\"epicTaskId\":2,\"id\":3,\"title\":\"firstSub1\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":21,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":4,\"title\":\"firstSub2\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":36,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":5,\"title\":\"firstSub3\",\"description\":\"\",\"status\":\"New\",\"duration\":0},{\"epicTaskId\":6,\"id\":7,\"title\":\"firstSub4\",\"description\":\"\",\"status\":\"New\",\"duration\":0},{\"epicTaskId\":2,\"id\":8,\"title\":\"subTaskForTest\",\"description\":\"test test\",\"status\":\"New\",\"duration\":0}]",
                createClient("subtask").body(),
                "Добавление Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testUpgradeNormalTask() {
        super.testUpgradeNormalTask();
        Assertions.assertEquals("[{\"id\":1,\"title\":\"testNormaltest\",\"description\":\"testtest\",\"status\":\"In_progress\",\"duration\":0}]",
                createClient("task").body(),
                "Обновление Нормала не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testUpgradeSubTask() {
        super.testUpgradeSubTask();
        Assertions.assertEquals("[{\"epicTaskId\":2,\"id\":3,\"title\":\"subTest\",\"description\":\"test\",\"status\":\"In_progress\",\"duration\":0},{\"epicTaskId\":2,\"id\":4,\"title\":\"firstSub2\",\"description\":\"\",\"status\":\"New\",\"startTime\":{\"date\":{\"year\":2023,\"month\":1,\"day\":6},\"time\":{\"hour\":19,\"minute\":36,\"second\":29,\"nano\":903000000}},\"duration\":15},{\"epicTaskId\":2,\"id\":5,\"title\":\"firstSub3\",\"description\":\"\",\"status\":\"New\",\"duration\":0},{\"epicTaskId\":6,\"id\":7,\"title\":\"firstSub4\",\"description\":\"\",\"status\":\"New\",\"duration\":0}]",
                createClient("subtask").body(),
                "Обновление Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testUpgradeEpicTask() {
        super.testUpgradeEpicTask();
        Assertions.assertEquals("[{\"subTasks\":[],\"id\":2,\"title\":\"testEpictest\",\"description\":\"testtest\",\"status\":\"New\",\"duration\":0},{\"subTasks\":[7],\"id\":6,\"title\":\"secondEpic\",\"description\":\"\",\"status\":\"New\",\"duration\":0}]",
                createClient("epic").body(),
                "Обновление Эпика не правильно отражено на сервере!");
    }
}

