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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    void testLoadFromFile() {
        FileBackedTasksManager testManager = FileBackedTasksManager.loadFromFile(Paths.get("testhistory.csv"));
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
                FileBackedTasksManager.loadFromFile(Paths.get("testhistoryisempty.csv"));

        Assertions.assertTrue(testManager.getHistory().isEmpty());
    }

    @Test
    void testLoadFromFileIsEmptyEpic() {
        FileBackedTasksManager testManager =
                FileBackedTasksManager.loadFromFile(Paths.get("testhistoryisemptyepic.csv"));

        Assertions.assertTrue(testManager.getByIdEpicTask(6).getSubTasks().isEmpty()
                ,"Эпик id 6 имеет не пустой лист сабов");
        Assertions.assertTrue(testManager.epicTasks.containsKey(6)
                ,"Эпик id 6 отсутствует в мапе эпиков");
    }

    @Test
    void testLoadFromFileHistoryWithoutTask() {
        FileBackedTasksManager testManager =
                FileBackedTasksManager.loadFromFile(Paths.get("testhistorywithouttassk.csv"));

        Assertions.assertTrue(testManager.getAllNormalTasks().isEmpty(),"");
        Assertions.assertTrue(testManager.getAllSubTasks().isEmpty(),"");
        Assertions.assertTrue(testManager.getAllEpicTasks().isEmpty(),"");
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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());

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
        Assertions.assertEquals(testCSV, loadCSVForTest());

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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());

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
        Assertions.assertEquals(testCSV, loadCSVForTest());

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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());
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
        Assertions.assertEquals(testCSV, loadCSVForTest());

    }

    @Test
    void testChangeStatusEpicTask() {
        super.testChangeStatusEpicTask();

    }

}