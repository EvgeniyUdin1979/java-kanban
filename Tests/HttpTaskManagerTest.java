import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmangers.HttpTaskManager;
import taskmangers.erros.ManagerLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class HttpTaskManagerTest extends TaskManagerTest {

    HttpTaskManager httpTaskManager = new HttpTaskManager("localhost");

    public HttpTaskManagerTest() {
        httpTaskManager.startServers();
        super.setManager(httpTaskManager);
    }

    @BeforeEach
    public void deleteCSV(){
        try {
            Files.deleteIfExists(Path.of("history.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void stop() {
        httpTaskManager.stopServers();
    }

    @Test
    @Override
    void testClearAllTasks() {
        super.testClearAllTasks();
        String testCSV = "Normal,0\n" +
                "id,title,description,status,startTime,duration\n" +
                "Epic,0\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "Sub,0\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "History,0\n" +
                "id\n";
        Assertions.assertThrows(ManagerLoadException.class,
                () -> {
            httpTaskManager.load();
                },"Не выброшено исключение на отсутствие файла сохраненной на диске истории, " +
                        "после не удачной загрузки с сервера.");
    }

    @Test
    @Override
    void testDeleteNormalTaskById() {
        super.testDeleteNormalTaskById();
        String testCSV = "Normal,0\n" +
                "id,title,description,status,startTime,duration\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "2,6";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Удаление Нормала не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testDeleteEpicTaskById() {
        super.testDeleteEpicTaskById();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,1\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,1\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,1\n" +
                "id\n" +
                "6";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Удаление Эпика не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testDeleteSubTaskById() {
        super.testDeleteSubTaskById();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,3\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "6,2";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Удаление Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testGetByIdNormalTask() {
        super.testGetByIdNormalTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,3\n" +
                "id\n" +
                "2,6,1";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Получение Нормала не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testGetByIdEpicTask() {
        super.testGetByIdEpicTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "6,2";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Получение Эпика не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testGetByIdSubTask() {
        super.testGetByIdSubTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,3\n" +
                "id\n" +
                "2,6,3";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Получение Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testAddNormalTask() {
        super.testAddNormalTask();
        String testCSV = "Normal,2\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "8,normalTest,test test,New,1673035589903,20\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,3\n" +
                "id\n" +
                "2,6,8";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Добавление Нормала не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testAddEpicTask() {
        super.testAddEpicTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,3\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "8,epicTest,test test,New,null,0\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,3\n" +
                "id\n" +
                "2,6,8";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Добавление Эпика не правильно отражено на сервере!");
    }
    @Test
    @Override
    void testChangeStatusEpicTask() {
        super.testChangeStatusEpicTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,3\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "8,epicTest,test test,In_progress,null,0,9,10,11\n" +
                "Sub,7\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "9,subTaskForTest1,,In_progress,null,0,8\n" +
                "10,subTaskForTest2,,In_progress,null,0,8\n" +
                "11,subTaskForTest3,,In_progress,null,0,8\n" +
                "History,6\n" +
                "id\n" +
                "2,6,8,9,10,11";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Изменение статуса Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testAddSubTask() {
        super.testAddSubTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5,8\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,5\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "8,subTaskForTest,test test,New,null,0,2\n" +
                "History,3\n" +
                "id\n" +
                "6,8,2";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Добавление Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testUpgradeNormalTask() {
        super.testUpgradeNormalTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,testNormaltest,testtest,In_progress,null,0\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,3\n" +
                "id\n" +
                "2,6,1";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Обновление Нормала не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testUpgradeSubTask() {
        super.testUpgradeSubTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,In_progress,1673033788903,15,4,5,3\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,subTest,test,In_progress,null,0,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,3\n" +
                "id\n" +
                "6,3,2";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Обновление Саба не правильно отражено на сервере!");
    }

    @Test
    @Override
    void testUpgradeEpicTask() {
        super.testUpgradeEpicTask();
        String testCSV = "Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,testEpictest,testtest,New,null,0\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,1\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "6,2";
        Assertions.assertEquals(testCSV, httpTaskManager.load(),
                "Обновление Эпика не правильно отражено на сервере!");
    }
}

