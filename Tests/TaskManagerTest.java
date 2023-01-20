import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;
import taskmangers.TaskManager;
import taskmangers.erros.ManagerIllegalIdException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static storetasks.StatusTask.*;

abstract class TaskManagerTest {
    //startTime for all test Task 1673021189903
    static Clock startTestTime = Clock.fixed(Instant.ofEpochMilli(1673021189903L), ZoneId.systemDefault());
    TaskManager manager;


    public TaskManager getManager() {
        return manager;
    }

    public void setManager(TaskManager manager) {
        this.manager = manager;
    }

    @BeforeEach
    public void createTasks() {
        NormalTask normalTask = new NormalTask("firstNormal", New, LocalDateTime.now(startTestTime), 15L);
        manager.addNormalTask(normalTask);
        EpicTask epicTask = new EpicTask("firstEpic");
        manager.addEpicTask(epicTask);
        EpicTask epicTaskOne = manager.getByIdEpicTask(2);
        SubTask subTask1 = new SubTask("firstSub1", New, LocalDateTime.now(startTestTime).plusMinutes(15), 15L, epicTaskOne.getId());
        SubTask subTask2 = new SubTask("firstSub2", New, LocalDateTime.now(startTestTime).plusMinutes(30), 15L, epicTaskOne.getId());
        SubTask subTask3 = new SubTask("firstSub3", New, epicTaskOne.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
        EpicTask epicTask2 = new EpicTask("secondEpic");
        manager.addEpicTask(epicTask2);
        EpicTask epicTaskTwo = manager.getByIdEpicTask(epicTask2.getId());
        SubTask subTask4 = new SubTask("firstSub4", New, epicTaskTwo.getId());
        manager.addSubTask(subTask4);
    }

    @Test
    void testGetAllNormalTasks() {
        List<NormalTask> taskList = manager.getAllNormalTasks();
        Assertions.assertEquals(1, taskList.size(), "Колличество тасков не равно 1");
        Assertions.assertNotNull(taskList, "Вернулся пустой список");

    }

    @Test
    void testGetAllEpicTasks() {
        List<EpicTask> taskList = manager.getAllEpicTasks();
        Assertions.assertNotNull(taskList, "Вернулся пустой список");
        Assertions.assertEquals(2, taskList.size(), "Колличество тасков не равно 2");
    }

    @Test
    void testGetAllSubTasks() {
        List<SubTask> taskList = manager.getAllSubTasks();
        Assertions.assertNotNull(taskList, "Вернулся пустой список");
        Assertions.assertEquals(4, taskList.size(), "Колличество тасков не равно 4");
    }

    @Test
    void testClearAllTasks() {
        manager.clearAllTasks();
        int quantityTask = manager.getAllNormalTasks().size()
                + manager.getAllEpicTasks().size()
                + manager.getAllSubTasks().size();
        Assertions.assertEquals(0, quantityTask, "Удалены не все таски! осталось : " + quantityTask);
    }

    @Test
    void testDeleteNormalTaskById() {
        manager.deleteNormalTaskById(1);
        Assertions.assertFalse(manager.getAllNormalTasks().stream().anyMatch(normalTask -> normalTask.getId() == 1),
                "Таск с номером 1 не удален из мапы");
        Assertions.assertFalse(manager.getHistory().stream().anyMatch(task -> task.getId() == 1),
                "Таск с номером 1 не удален из истории");
        Assertions.assertFalse(manager.getPrioritizedTasks().stream().anyMatch(task -> task.getId() == 1),
                "Таск с номером 1 не удален из листа приоритетов");
    }

    @Test
    void testDeleteNormalTaskByIdByTheWrongId() {
        IllegalArgumentException incorrectIDException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    manager.deleteNormalTaskById(2);
                }, "Не выброшено исключение на удаление Нормала c не верным id!");
        Assertions.assertEquals("Попытка удалить Нормала по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testDeleteEpicTaskById() {
        final List<Integer> subTasksEpicTask = manager.getByIdEpicTask(2).getSubTasks();
        manager.deleteEpicTaskById(2);
        Assertions.assertFalse(manager.getAllEpicTasks().stream().anyMatch(epicTask -> epicTask.getId() == 2)
                , "Таск с номером 2 не удален из мапы");
        Assertions.assertFalse(manager.getHistory().stream().anyMatch(task -> task.getId() == 2)
                , "Таск с номером 2 не удален из истории");
        Assertions.assertFalse(manager.getPrioritizedTasks().stream().anyMatch(task -> task.getId() == 2)
                , "Таск с номером 2 не удален из листа приоритетов");
        if (!subTasksEpicTask.isEmpty()) {
            Assertions.assertFalse(manager.getAllSubTasks().stream()
                            .anyMatch(subTask -> subTasksEpicTask.contains(subTask.getId()))
                    , "После удаления Эпика не все сабтаски были удалены из мапы");
            Assertions.assertFalse(manager.getHistory().stream()
                            .anyMatch(task -> subTasksEpicTask.contains(task.getId()))
                    , "После удаления Эпика не все сабтаски были удалены из истории");
            Assertions.assertFalse(manager.getPrioritizedTasks().stream()
                            .anyMatch(task -> subTasksEpicTask.contains(task.getId()))
                    , "После удаления Эпика не все сабтаски были удалены из листа приоритетов");
        }
    }

    @Test
    void testDeleteEpicTaskByIdByTheWrongId() {
        IllegalArgumentException incorrectIDException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    manager.deleteEpicTaskById(1);
                }, "Не выброшено исключение на удаление Эпика c не верным id!");
        Assertions.assertEquals("Попытка удалить Эпика по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testDeleteSubTaskById() {
        EpicTask epicTask = manager.getByIdEpicTask(manager.getByIdSubTask(3).getEpicTaskId());
        manager.deleteSubTaskById(3);
        Assertions.assertFalse(manager.getAllSubTasks().stream().anyMatch(subTask -> subTask.getId() == 3)
                , "Сабтаска не была удалена из мапы");
        Assertions.assertFalse(manager.getHistory().stream().anyMatch(subTask -> subTask.getId() == 3)
                , "Сабтаска не была удалена из истории");
        Assertions.assertFalse(epicTask.getSubTasks().contains(3)
                , "Сабтаска не была удалена из листа сабов соответствующего эпика!");
        Assertions.assertFalse(manager.getPrioritizedTasks().stream().anyMatch(subTask -> subTask.getId() == 3)
                , "Сабтаска не была удалена из листа приоритетов");
    }

    @Test
    void testDeleteSubTaskByIdByTheWrongId() {
        IllegalArgumentException incorrectIDException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    manager.deleteSubTaskById(1);
                }, "Не выброшено исключение на удаление Саба c не верным id!");
        Assertions.assertEquals("Попытка удалить Саба по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testGetByIdNormalTask() {
        Assertions.assertEquals(1, manager.getByIdNormalTask(1).getId()
                , "Id полученной Нормала отличается от 1");
        Assertions.assertTrue(manager.getHistory().stream().anyMatch(task -> task.getId() == 1),
                "Получение по id Нормала не сохранилось в истории!");
        IllegalArgumentException incorrectIDException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    manager.getByIdNormalTask(2);
                }, "Не выброшено исключение на получение Нормала c не верным id!");
        Assertions.assertEquals("Попытка получить Нормала по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testGetByIdEpicTask() {
        EpicTask epicTask = manager.getByIdEpicTask(2);
        Assertions.assertEquals(2, epicTask.getId()
                , "id полученного Эпика отличается от 2!");
        Assertions.assertTrue(manager.getHistory().stream().anyMatch(task -> task.getId() == 2)
                , "Получение по id Эпика не сохранилось в истории!");
        Assertions.assertFalse(manager.getHistory().stream()
                        .anyMatch(task -> epicTask.getSubTasks().contains(task.getId()))
                , "Сабтаски полученного по id эпика сохранены в истории!");
        IllegalArgumentException incorrectIDException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    manager.getByIdEpicTask(1);
                }, "Не выброшено исключение на получение Эпика c не верным id!");
        Assertions.assertEquals("Попытка получить Эпика по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");

    }

    @Test
    void testGetByIdSubTask() {
        Assertions.assertEquals(3, manager.getByIdSubTask(3).getId()
                , "Получен таск с id отличным от 3!");
        Assertions.assertTrue(manager.getHistory().stream().anyMatch(task -> task.getId() == 3)
                , "Получение Сабтаски не отразилось в истории!");
        IllegalArgumentException incorrectIDException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    manager.getByIdSubTask(2);
                }, "Не выброшено исключение на получение Саба c не верным id!");
        Assertions.assertEquals("Попытка получить Саба по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testAddNormalTask() {
        NormalTask normalTask = new NormalTask("normalTest"
                , "test test"
                , New
                , LocalDateTime.now(startTestTime).plusHours(1)
                , 20);
        manager.addNormalTask(normalTask);
        NormalTask normalTaskForTest = new NormalTask("normalTest"
                , "test test"
                , New
                , LocalDateTime.now(startTestTime).plusHours(1)
                , 20);
        normalTaskForTest.setId(8);
        Assertions.assertEquals(normalTaskForTest, manager.getByIdNormalTask(8));
        Assertions.assertTrue(manager.getPrioritizedTasks().stream().anyMatch(subTask -> subTask.getId() == 8)
                , "Нормал не был добавлен в листа приоритетов");
        IllegalArgumentException checkingTheIntersectionOfTimeSegmentsException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    NormalTask normalTask1 = new NormalTask("normalTest"
                            , "test test"
                            , New
                            , LocalDateTime.now(startTestTime).plusHours(1)
                            , 20);
                    manager.addNormalTask(normalTask1);
                }, "Не выброшено исключение на пересечение отрезков времени при добавлении Нормала!");
        Assertions.assertEquals("Пересечение отрезков времени!", checkingTheIntersectionOfTimeSegmentsException.getMessage()
                , "У исключения не правильное сообщение");
        RuntimeException exceptionIdNotZero = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    NormalTask normalTask1 = new NormalTask("normalTest"
                            , "test test"
                            , New
                            , LocalDateTime.now(startTestTime).plusHours(1)
                            , 20);
                    normalTask1.setId(1);
                    manager.addNormalTask(normalTask1);
                }, "Не выброшено исключение на не нулевой id при добавлении Нормала!");
        Assertions.assertEquals(NormalTask.class.getSimpleName() + "Id отличается от начального: 1", exceptionIdNotZero.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testAddEpicTask() {
        EpicTask epicTask = new EpicTask("epicTest", "test test");
        manager.addEpicTask(epicTask);
        EpicTask epicTaskForTest = new EpicTask("epicTest", "test test");
        epicTaskForTest.setId(8);
        epicTaskForTest.setStatus(New);
        Assertions.assertEquals(epicTaskForTest, manager.getByIdEpicTask(8), "Эпик небыл добавлен в мапу");
        Assertions.assertTrue(manager.getPrioritizedTasks().stream().anyMatch(subTask -> subTask.getId() == 8)
                , "Эпик не был добавлен в листа приоритетов");
        RuntimeException exceptionIdNotZero = Assertions.assertThrows(RuntimeException.class,
                () -> {

                    epicTaskForTest.setId(12);
                    manager.addEpicTask(epicTaskForTest);
                }, "Не выброшено исключение на не нулевой id при добавлении Нормала!");
        Assertions.assertEquals(EpicTask.class.getSimpleName() + "Id отличается от начального: 12", exceptionIdNotZero.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testChangeStatusEpicTask() {
        EpicTask epicTaskForTest = new EpicTask("epicTest", "test test");
        manager.addEpicTask(epicTaskForTest);
        epicTaskForTest = manager.getByIdEpicTask(epicTaskForTest.getId());
        Assertions.assertTrue(epicTaskForTest.getSubTasks().isEmpty(), "Сабов больше чем 0");
        Assertions.assertSame(epicTaskForTest.getStatus(), New, "Неправильный статус, должен быть New");

        SubTask subTaskForTest1 = new SubTask("subTaskForTest1", New, epicTaskForTest.getId());
        SubTask subTaskForTest2 = new SubTask("subTaskForTest2", New, epicTaskForTest.getId());
        SubTask subTaskForTest3 = new SubTask("subTaskForTest3", New, epicTaskForTest.getId());
        manager.addSubTask(subTaskForTest1);
        manager.addSubTask(subTaskForTest2);
        manager.addSubTask(subTaskForTest3);
        Assertions.assertSame(epicTaskForTest.getStatus(), New, "Неправильный статус, должен быть New");

        subTaskForTest1 = manager.getByIdSubTask(subTaskForTest1.getId());
        subTaskForTest2 = manager.getByIdSubTask(subTaskForTest2.getId());
        subTaskForTest3 = manager.getByIdSubTask(subTaskForTest3.getId());
        subTaskForTest1.setStatus(Done);
        subTaskForTest2.setStatus(Done);
        subTaskForTest3.setStatus(Done);
        manager.upgradeSubTask(subTaskForTest1);
        manager.upgradeSubTask(subTaskForTest2);
        manager.upgradeSubTask(subTaskForTest3);
        Assertions.assertSame(epicTaskForTest.getStatus(), Done, "Неправильный статус, должен быть Done");

        subTaskForTest1 = manager.getByIdSubTask(subTaskForTest1.getId());
        subTaskForTest2 = manager.getByIdSubTask(subTaskForTest2.getId());
        subTaskForTest3 = manager.getByIdSubTask(subTaskForTest3.getId());
        subTaskForTest1.setStatus(New);
        subTaskForTest2.setStatus(New);
        subTaskForTest3.setStatus(Done);
        manager.upgradeSubTask(subTaskForTest1);
        manager.upgradeSubTask(subTaskForTest2);
        manager.upgradeSubTask(subTaskForTest3);
        Assertions.assertSame(epicTaskForTest.getStatus(), In_progress, "Неправильный статус, должен быть In_progress");

        subTaskForTest1 = manager.getByIdSubTask(subTaskForTest1.getId());
        subTaskForTest2 = manager.getByIdSubTask(subTaskForTest2.getId());
        subTaskForTest3 = manager.getByIdSubTask(subTaskForTest3.getId());
        subTaskForTest1.setStatus(In_progress);
        subTaskForTest2.setStatus(In_progress);
        subTaskForTest3.setStatus(In_progress);
        manager.upgradeSubTask(subTaskForTest1);
        manager.upgradeSubTask(subTaskForTest2);
        manager.upgradeSubTask(subTaskForTest3);
        Assertions.assertSame(epicTaskForTest.getStatus(), In_progress, "Неправильный статус, должен быть In_progress");
    }

    @Test
    void testAddSubTask() {
        SubTask subTask = new SubTask("subTaskForTest", "test test", New, 2);
        manager.addSubTask(subTask);
        SubTask subTaskForTest = new SubTask("subTaskForTest", "test test", New, 2);
        subTaskForTest.setId(8);
        Assertions.assertEquals(subTaskForTest, manager.getByIdSubTask(8), "Сабка не добавилась в мапу");
        Assertions.assertTrue(manager.getPrioritizedTasks().stream().anyMatch(sub -> sub.getId() == 8)
                , "Сабка не был добавлен в лист приоритетов");
        EpicTask epicTask = manager.getByIdEpicTask(2);
        Assertions.assertTrue(epicTask.getSubTasks().contains(8), "Сабка не добавилась в лист к Эпику");

        RuntimeException exceptionIdNotZero = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    subTaskForTest.setId(12);
                    manager.addSubTask(subTaskForTest);
                }, "Не выброшено исключение на не нулевой id при добавлении Нормала!");
        Assertions.assertEquals(SubTask.class.getSimpleName() + " Id отличается от начального: 12", exceptionIdNotZero.getMessage()
                , "У исключения не правильное сообщение");

        IllegalArgumentException checkingTheIntersectionOfTimeSegmentsException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    subTaskForTest.setId(0);
                    subTaskForTest.setStartTime(LocalDateTime.now(startTestTime).plusMinutes(20));
                    subTaskForTest.setDuration(20);
                    manager.addSubTask(subTaskForTest);
                }, "Не выброшено исключение на пересечение отрезков времени при добавлении Саба!");
        Assertions.assertEquals("Пересечение отрезков времени!", checkingTheIntersectionOfTimeSegmentsException.getMessage()
                , "У исключения не правильное сообщение");

    }

    @Test
    void testUpgradeNormalTask() {
        NormalTask oldNormalTask = manager.getByIdNormalTask(1);
        NormalTask normalTaskForUpdate = new NormalTask("testNormaltest", "testtest", In_progress);
        normalTaskForUpdate.setId(1);
        manager.upgradeNormalTask(normalTaskForUpdate);
        Assertions.assertNotEquals(oldNormalTask, normalTaskForUpdate
                ,"После обновления Нормал должны отличатся!");
        NormalTask normalTaskForTest = new NormalTask("testNormaltest", "testtest", In_progress);
        normalTaskForTest.setId(1);
        Assertions.assertEquals(normalTaskForTest, manager.getByIdNormalTask(1), "");
        List <Task> listPrioritizedTasks = manager.getPrioritizedTasks();
        Assertions.assertFalse(listPrioritizedTasks.contains(oldNormalTask)
                ,"После обновления старый Нормал не  должен находится в листе приоритетов!");
        Assertions.assertTrue(listPrioritizedTasks.contains(manager.getByIdNormalTask(1))
                ,"После обновления новый Нормал  должен находится в листе приоритетов!");
        ManagerIllegalIdException incorrectIDException = Assertions.assertThrows(ManagerIllegalIdException.class,
                () -> {
                    normalTaskForTest.setId(2);
                    manager.upgradeNormalTask(normalTaskForTest);
                }, "Не выброшено исключение на обновление Нормала c не верным id!");
        Assertions.assertEquals("Попытка обновления Нормала по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");
        IllegalArgumentException checkingTheIntersectionOfTimeSegmentsException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    NormalTask normalTask1 = new NormalTask("normalTest"
                            , "test test"
                            , New
                            , LocalDateTime.now(startTestTime).plusMinutes(20)
                            , 20);
                    normalTask1.setId(1);
                    manager.upgradeNormalTask(normalTask1);
                }, "Не выброшено исключение на пересечение отрезков времени при обновлении Нормала!");
        Assertions.assertEquals("Пересечение отрезков времени!", checkingTheIntersectionOfTimeSegmentsException.getMessage()
                , "У исключения не правильное сообщение");

    }


    @Test
    void testUpgradeSubTask() {
        SubTask subTaskForUpdate = new SubTask("subTest", "test", In_progress, 2);
        subTaskForUpdate.setId(3);
        manager.upgradeSubTask(subTaskForUpdate);
        SubTask subTaskForTest = new SubTask("subTest", "test", In_progress, 2);
        subTaskForTest.setId(3);
        Assertions.assertEquals(subTaskForTest, manager.getByIdSubTask(3));
        Assertions.assertSame(manager.getByIdEpicTask(2).getStatus(), In_progress);

        ManagerIllegalIdException incorrectIDException = Assertions.assertThrows(ManagerIllegalIdException.class,
                () -> {
                    subTaskForTest.setId(2);
                    manager.upgradeSubTask(subTaskForTest);
                }, "Не выброшено исключение на обновление Саба c не верным id!");
        Assertions.assertEquals("Попытка обновления Саба по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");

        IllegalArgumentException checkingTheIntersectionOfTimeSegmentsException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    subTaskForTest.setId(3);
                    subTaskForTest.setStartTime(LocalDateTime.now(startTestTime).plusMinutes(40));
                    subTaskForTest.setDuration(20);
                    manager.upgradeSubTask(subTaskForTest);
                }, "Не выброшено исключение на пересечение отрезков времени при обновлении Саба!");
        Assertions.assertEquals("Пересечение отрезков времени!", checkingTheIntersectionOfTimeSegmentsException.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testUpgradeEpicTask() {
        List<Integer> subTasksEpicTask = manager.getByIdEpicTask(2).getSubTasks();
        EpicTask epicTaskForUpdate = new EpicTask("testEpictest", "testtest");
        epicTaskForUpdate.setId(2);
        manager.upgradeEpicTask(epicTaskForUpdate);
        EpicTask epicTaskForTest = new EpicTask("testEpictest", "testtest", New);
        epicTaskForTest.setId(2);
        Assertions.assertEquals(epicTaskForTest, manager.getByIdEpicTask(2));
        if (!subTasksEpicTask.isEmpty()) {
            Assertions.assertFalse(manager.getAllSubTasks().stream()
                    .anyMatch(subTask -> subTasksEpicTask.contains(subTask.getId())));
            Assertions.assertFalse(manager.getHistory().stream()
                    .anyMatch(task -> subTasksEpicTask.contains(task.getId())));
        }
        ManagerIllegalIdException incorrectIDException = Assertions.assertThrows(ManagerIllegalIdException.class,
                () -> {
                    epicTaskForTest.setId(3);
                    manager.upgradeEpicTask(epicTaskForTest);
                }, "Не выброшено исключение на обновление Эпика c не верным id!");
        Assertions.assertEquals("Попытка обновления Эпика по несуществующему id", incorrectIDException.getMessage()
                , "У исключения не правильное сообщение");
    }

    @Test
    void testGetHistory() {
        List<Task> historyForTest = manager.getHistory();
        List<Task> listForTest = List.of(manager.getByIdEpicTask(2), manager.getByIdEpicTask(6));
        Assertions.assertEquals(historyForTest, listForTest);

    }

    @Test
    void testGetPrioritizedTasks(){
        List<Task> listPrioritizedTasks = manager.getPrioritizedTasks();
        Assertions.assertNotNull(listPrioritizedTasks,"getPrioritizedTasks() возвращает null");
        Assertions.assertFalse(listPrioritizedTasks.isEmpty()
                ,"getPrioritizedTasks() возвращает пустой лист");
       Assertions.assertSame(listPrioritizedTasks.size(),7
               ,"getPrioritizedTasks() возвращает лист не правильного размера!");


    }

}