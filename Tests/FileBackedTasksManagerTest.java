import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import storetasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class FileBackedTasksManagerTest extends TaskManagerTest {
    FileBackedTasksManager manager;

    public FileBackedTasksManagerTest() {
        manager = new FileBackedTasksManager();
        super.setManager(manager);
    }

    @Test
    void save() {
        manager.save();
        String str = "Normal,1\n" +
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
                "2,6";
        Assertions.assertEquals(str, loadCSVForTest());

    }

    private String loadCSVForTest() {
        try {
            return Files.readString(Paths.get("history.csv"));
        } catch (IOException ignore) {}

        return null;
    }

    @Test
    void testLoadFromFile() {
        FileBackedTasksManager testManager = FileBackedTasksManager.loadFromFile(Paths.get("TestRes/testhistory.csv"));
        List<Integer> testNormal = List.of(1);
        List<Integer> testEpic = List.of(2, 6);
        List<Integer> testSub = List.of(3, 4, 5, 7);
        List<Task> testHistory = List.of(manager.getByIdEpicTask(2), manager.getByIdEpicTask(6));
        List<Task> testHistoryFromManager = testManager.getHistory();
        Assertions.assertTrue(testManager.getAllNormalTasks().stream()
                .anyMatch(task -> testNormal.contains(task.getId())));
        Assertions.assertTrue(testManager.getAllEpicTasks().stream()
                .anyMatch(task -> testEpic.contains(task.getId())));
        Assertions.assertTrue(testManager.getAllSubTasks().stream()
                .anyMatch(task -> testSub.contains(task.getId())));
        Assertions.assertEquals(testHistory, testHistoryFromManager);
    }

    @Test
    void testLoadFromFileHistoryIsEmpty() {
        FileBackedTasksManager testManager =
                FileBackedTasksManager.loadFromFile(Paths.get("TestRes/testhistoryisempty.csv"));

        Assertions.assertTrue(testManager.getHistory().isEmpty());
    }

    @Test
    void testLoadFromFileHistoryErrorInNormal() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileBackedTasksManager.loadFromFile(Paths.get("TestRes/errorindata/testhistoryerrorinnormal.csv")),
                "Не выброшено исключение при ошибке в сроке файла истории");
        Assertions.assertEquals("Не верное число элементов в строке Нормал тасков",
                exception.getMessage(),"Сообщение исключения не правильное!");
    }

    @Test
    void testLoadFromFileHistoryErrorInEpic() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileBackedTasksManager.loadFromFile(Paths.get("TestRes/errorindata/testhistoryerrorinepic.csv")),
                "Не выброшено исключение при ошибке в сроке файла истории");
        Assertions.assertEquals("Не верное число элементов в строке Эпик тасков",
                exception.getMessage(),"Сообщение исключения не правильное!");
    }

    @Test
    void testLoadFromFileHistoryErrorInSub() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileBackedTasksManager.loadFromFile(Paths.get("TestRes/errorindata/testhistoryerrorinsub.csv")),
                "Не выброшено исключение при ошибке в сроке файла истории");
        Assertions.assertEquals("Не верное число элементов в строке Саб тасков",
                exception.getMessage(),"Сообщение исключения не правильное!");
    }

    @Test
    void testLoadFromFileHistoryErrorInHistory() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileBackedTasksManager.loadFromFile(Paths.get("TestRes/errorindata/testhistoryerrorinhistory.csv")),
                "Не выброшено исключение при ошибке в сроке файла истории");
        Assertions.assertEquals("Не верное число элементов в строке History",
                exception.getMessage(),"Сообщение исключения не правильное!");
    }

    @Test
    void testLoadFromFileHistoryErrorInType() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileBackedTasksManager.loadFromFile(Paths.get("TestRes/errorindata/testhistoryerrorintype.csv")),
                "Не выброшено исключение при ошибке в сроке файла истории");
        Assertions.assertEquals("Не верное число элементов в строке Type",
                exception.getMessage(),"Сообщение исключения не правильное!");
    }

    @Test
    void testLoadFromFileIsEmptyEpic() {
        FileBackedTasksManager testManager =
                FileBackedTasksManager.loadFromFile(Paths.get("TestRes/testhistoryisemptyepic.csv"));

        Assertions.assertTrue(testManager.getByIdEpicTask(6).getSubTasks().isEmpty()
                ,"Эпик id 6 имеет не пустой лист сабов");
        Assertions.assertTrue(testManager.epicTasks.containsKey(6)
                ,"Эпик id 6 отсутствует в мапе эпиков");
    }

    @Test
    void testLoadFromFileHistoryWithoutTaskAndNotEmptyHistory() {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> FileBackedTasksManager.loadFromFile(Paths.get("TestRes/testhistorywithouttassk.csv")),
                "Не выброшено исключение отсутствие загруженных Тасков при не пучтой истории");
        Assertions.assertEquals("Таск 2 не был восстановлен из файла!",
                exception.getMessage(),"Сообщение исключения не правильное!");
    }

    @Test
    void testLoadFromFileHistoryWithoutTaskAndEmptyHistory() {
        FileBackedTasksManager testManager =
                FileBackedTasksManager.loadFromFile(Paths.get("TestRes/testhistoryisemptyall.csv"));

        Assertions.assertTrue(testManager.getAllNormalTasks().isEmpty(),"NormalTasks не пустой");
        Assertions.assertTrue(testManager.getAllSubTasks().isEmpty(),"SubTasks не пустой");
        Assertions.assertTrue(testManager.getAllEpicTasks().isEmpty(),"EpicTasks не пустой");
        Assertions.assertTrue(testManager.getHistory().isEmpty(),"History не пустой");
        Assertions.assertTrue(testManager.getPrioritizedTasks().isEmpty(),"PrioritizedTasks не пустой");

    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Удаление Нормала не правильно отражено в файле");
    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Удаление Эпика не правильно отражено в файле");
    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Удаление Саба не правильно отражено в файле");

    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Добавление Нормала не правильно отражено в файле");

    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Добавление Эпика не правильно отражено в файле");
    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Добавление Саба не правильно отражено в файле");

    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Обновление Нормала не правильно отражено в файле");

    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Обновление Саба не правильно отражено в файле");
    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Обновление Эпика не правильно отражено в файле");
    }

    @Test
    void testClearAllTasks() {
        super.testClearAllTasks();
        String testCSV = "Normal,0\n" +
                "id,title,description,status,startTime,duration\n" +
                "Epic,0\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "Sub,0\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "History,2\n" +
                "id\n" +
                "2,6";
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Удаление всех тасков не правильно отбражено в файле");
    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Получение Нормала не правильно отражено в файле");
    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Получение Эпика не правильно отражено в файле");
    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Получение Саба не правильно отражено в файле");

    }

    @Test
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
        Assertions.assertEquals(testCSV, loadCSVForTest(),
                "Изменение статуса Саба не правильно отражено в файле");
    }

}