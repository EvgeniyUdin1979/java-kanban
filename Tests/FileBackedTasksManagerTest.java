import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import storetasks.*;

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
        Assertions.assertEquals(str,loadCSVForTest());

    }
    private String loadCSVForTest(){
        try {
            return Files.readString(Paths.get("history.csv"));
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }



    @Test
    void testLoadFromFile() {
       FileBackedTasksManager testManager =  FileBackedTasksManager.loadFromFile(Paths.get("history.csv"));
        List<Integer> testNormal = List.of(1);
        List<Integer> testEpic = List.of(2,6);
        List<Integer> testSub = List.of(3,4,5,7);
        List<Task> testHistory = List.of(manager.getByIdEpicTask(2),manager.getByIdEpicTask(6));
        List<Task> testHistoryFromManager = testManager.getHistory();
        Assertions.assertTrue(testManager.getAllNormalTasks().stream()
                .anyMatch(task -> testNormal.contains(task.getId())));
        Assertions.assertTrue(testManager.getAllEpicTasks().stream()
                .anyMatch(task -> testEpic.contains(task.getId())));
        Assertions.assertTrue(testManager.getAllSubTasks().stream()
                .anyMatch(task -> testSub.contains(task.getId())));
        Assertions.assertEquals(testHistory,testHistoryFromManager);
    }

    @Test
    void testLoadFromFileHistoryIsEmpty() {
        FileBackedTasksManager testManager =
                FileBackedTasksManager.loadFromFile(Paths.get("testhistoryisempty.csv"));

        Assertions.assertTrue(testManager.getHistory().isEmpty());
    }

    @Test
    void testDeleteNormalTaskById() {
        manager.deleteNormalTaskById(1);
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
        Assertions.assertEquals(testCSV,loadCSVForTest());
    }

    @Test
    void testDeleteEpicTaskById() {
        manager.deleteEpicTaskById(2);
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
        Assertions.assertEquals(testCSV,loadCSVForTest());
    }

    @Test
    void testDeleteSubTaskById() {
        manager.deleteSubTaskById(3);
        String testCSV ="Normal,1\n" +
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
                "2,6";
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testAddNormalTask() {
        NormalTask testNormalTask = new NormalTask("test", "testtest", StatusTask.New);
        manager.addNormalTask(testNormalTask);
        String testCSV ="Normal,2\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "8,test,testtest,New,null,0\n" +
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
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testAddEpicTask() {
        EpicTask testEpicTask = new EpicTask("testEpic","testtest");
        manager.addEpicTask(testEpicTask);
        String testCSV ="Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,3\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "8,testEpic,testtest,New,null,0\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "2,6";
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testAddSubTask() {
        SubTask testSubTask = new SubTask("testSubTask","testtest",StatusTask.In_progress,6);
        manager.addSubTask(testSubTask);
        String testCSV ="Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,New,1673032888903,30,3,4,5\n" +
                "6,secondEpic,,In_progress,null,0,7,8\n" +
                "Sub,5\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,firstSub1,,New,1673032889903,15,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "8,testSubTask,testtest,In_progress,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "2,6";
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testUpgradeNormalTask() {
        NormalTask testNormalTask = new NormalTask("testNormal","testtest",StatusTask.In_progress);
        testNormalTask.setId(1);
        manager.upgradeNormalTask(testNormalTask);
        String testCSV ="Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,testNormal,testtest,In_progress,null,0\n" +
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
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testUpgradeSubTask() {
        SubTask testSubTask = new SubTask("testSubTask","testtest",StatusTask.In_progress,2);
        testSubTask.setId(3);
        manager.upgradeSubTask(testSubTask);
        String testCSV ="Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,firstEpic,,In_progress,1673033788903,15,4,5,3\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,4\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "3,testSubTask,testtest,In_progress,null,0,2\n" +
                "4,firstSub2,,New,1673033789903,15,2\n" +
                "5,firstSub3,,New,null,0,2\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "2,6";
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testUpgradeEpicTask() {
        EpicTask testEpicTask = new EpicTask("testEpicTask","testtest");
        testEpicTask.setId(2);
        manager.upgradeEpicTask(testEpicTask);
        String testCSV ="Normal,1\n" +
                "id,title,description,status,startTime,duration\n" +
                "1,firstNormal,,New,1673031989903,15\n" +
                "Epic,2\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "2,testEpicTask,testtest,New,null,0\n" +
                "6,secondEpic,,New,null,0,7\n" +
                "Sub,1\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "7,firstSub4,,New,null,0,6\n" +
                "History,2\n" +
                "id\n" +
                "2,6";
        Assertions.assertEquals(testCSV,loadCSVForTest());
    }

    @Test
    void testClearAllTasks() {
        manager.clearAllTasks();
        String testCSV ="Normal,0\n" +
                "id,title,description,status,startTime,duration\n" +
                "Epic,0\n" +
                "id,title,description,status,startTime,duration,subId\n" +
                "Sub,0\n" +
                "id,title,description,status,startTime,duration,epicId\n" +
                "History,2\n" +
                "id\n" +
                "2,6";
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testGetByIdNormalTask() {
        manager.getByIdNormalTask(1);
        String testCSV ="Normal,1\n" +
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
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testGetByIdEpicTask() {
        manager.getByIdEpicTask(2);
        String testCSV ="Normal,1\n" +
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
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

    @Test
    void testGetByIdSubTask() {
        manager.getByIdSubTask(3);
        String testCSV ="Normal,1\n" +
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
        Assertions.assertEquals(testCSV,loadCSVForTest());

    }

}