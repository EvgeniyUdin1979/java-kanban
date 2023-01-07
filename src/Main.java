import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static storetasks.StatusTask.*;

public class Main {
    static TaskManager manager =Managers.getDefault();

    static {
        NormalTask normalTask = new NormalTask("firstNormal", New, LocalDateTime.now(),10L);
        manager.addNormalTask(normalTask);
        EpicTask epicTask = new EpicTask("firstEpic");
        manager.addEpicTask(epicTask);
        EpicTask epicTaskOne = manager.getByIdEpicTask(2);
        SubTask subTask1 = new SubTask("firstSub1", New, LocalDateTime.now().plusMinutes(10), 10L,epicTaskOne.getId());
        SubTask subTask2 = new SubTask("firstSub2", New,LocalDateTime.now().plusMinutes(50), 20L,epicTaskOne.getId());
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

    static List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(manager.getAllEpicTasks());
        allTasks.addAll(manager.getAllNormalTasks());
        allTasks.addAll(manager.getAllSubTasks());
        return allTasks;
    }

    public static void main(String[] args) {
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Начальная распечатка");
        printHistory();
        manager.getPrioritizedTasks().forEach(x -> System.out.println(x.getId() + " : " + x.getStartTime() + " : " + x.getEndTime()));

        NormalTask normal1 = manager.getByIdNormalTask(1);
        normal1.setStatus(In_progress);
        normal1.setDescription("описание");
        manager.upgradeNormalTask(normal1);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение нормал №1");
        printHistory();

        SubTask subTask1 = manager.getByIdSubTask(3);
        subTask1.setStatus(In_progress);
        manager.upgradeSubTask(subTask1);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение саб №3");
        printHistory();

        SubTask subTask2 = manager.getByIdSubTask(4);
        subTask2.setStatus(Done);
        manager.upgradeSubTask(subTask2);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение саб №4");
        printHistory();

        SubTask subTask3 = manager.getByIdSubTask(5);
        subTask3.setStatus(Done);
        manager.upgradeSubTask(subTask3);
        EpicTask epicTask1 = manager.getByIdEpicTask(2);
        epicTask1.setDescription("Проверка обновления Эпика");
        manager.upgradeEpicTask(epicTask1);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение саб №5 и эпик №2");
        printHistory();

        manager.deleteNormalTaskById(1);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Удаление нормал №1");
        printHistory();

        NormalTask normalTask = new NormalTask("normal1", New);
        manager.addNormalTask(normalTask);
        getAllTasks().forEach(System.out::println);
        System.out.println("-----------------------------------------------Добавление нормал для проверки");
        printHistory();

        manager.deleteSubTaskById(3);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Удаление саб №3");
        printHistory();

        manager.deleteEpicTaskById(2);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Удаление эпик №2");
        printHistory();
        Random rnd = new Random();
        Task task;
        for (int i = 0; i < 30; i++) {
            switch (rnd.nextInt(3)) {
                case 0:
                    task = manager.getByIdNormalTask(8);
                    break;
                case 1:
                    task = manager.getByIdEpicTask(6);
                    break;
                case 2:
                    task = manager.getByIdSubTask(7);
                    break;
            }
        }
        System.out.println("---------------------------------------------------Трогаю оставшиеся №6, №7, №8");
        printHistory();
        EpicTask epicTask = new EpicTask("twoEpic");
        manager.addEpicTask(epicTask);
        EpicTask epicTaskTwo = manager.getByIdEpicTask(9);
        SubTask subTask_1 = new SubTask("firstSub1", New, LocalDateTime.now().plusMinutes(60), 15L,epicTaskTwo.getId());
        SubTask subTask_2 = new SubTask("firstSub2", New, LocalDateTime.now().plusMinutes(80), 15L,epicTaskTwo.getId());
        SubTask subTask_3 = new SubTask("firstSub3", New, LocalDateTime.now().plusMinutes(100), 15L,epicTaskTwo.getId());
        manager.addSubTask(subTask_1);
        manager.addSubTask(subTask_2);
        manager.addSubTask(subTask_3);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Добавляю эпик №9 и сабТаски №10, 11, 12");
        manager.getPrioritizedTasks().forEach(x -> System.out.println(x.getId()
                + " : "
                + x.getStartTime()
                + " : "
                + x.getEndTime()));

    }

    private static void printHistory() {
        int count = 0;
        for (Task taskInHistory : manager.getHistory()) {

            System.out.print(count++ + " : id - " + taskInHistory.getId() + " | ");
        }
        System.out.println("***history**");
    }
}
