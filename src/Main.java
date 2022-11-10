import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static storetasks.StatusTask.*;

public class Main {
    static TaskManager manager = Managers.getDefault();

    static {
        NormalTask normalTask = new NormalTask("firstNormal", New);
        manager.addNormalTask(normalTask);
        EpicTask epicTask = new EpicTask("firstEpic");
        manager.addEpicTask(epicTask);
        EpicTask epicTaskOne = manager.getByIdEpicTask(2);
        SubTask subTask1 = new SubTask("firstSub1", New, epicTaskOne.getId());
        SubTask subTask2 = new SubTask("firstSub2", New, epicTaskOne.getId());
        SubTask subTask3 = new SubTask("firstSub3", New, epicTaskOne.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
        EpicTask epicTask2 = new EpicTask("secondEpic");
        manager.addEpicTask(epicTask2);
        EpicTask epicTaskTwo = manager.getByIdEpicTask(6);
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

        NormalTask normal1 = manager.getByIdNormalTask(1);
        normal1.setStatus(In_progress);
        normal1.setDescription("описание");
        manager.upgradeNormalTask(normal1);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение нормал №1");

        SubTask subTask1 = manager.getByIdSubTask(3);
        subTask1.setStatus(In_progress);
        manager.upgradeSubTask(subTask1);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение саб №3");

        SubTask subTask2 = manager.getByIdSubTask(4);
        subTask2.setStatus(Done);
        manager.upgradeSubTask(subTask2);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение саб №4");

        SubTask subTask3 = manager.getByIdSubTask(5);
        subTask3.setStatus(Done);
        manager.upgradeSubTask(subTask3);
        EpicTask epicTask1 = manager.getByIdEpicTask(2);
        epicTask1.setDescription("Проверка обновления Эпика");
        manager.upgradeEpicTask(epicTask1);
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Изменение саб №5 и эпик №2");
//
//        if (manager.deleteNormalTaskById(1)) {
//            System.out.println("Удаление прошло успешно!");
//        }
//        getAllTasks().forEach(System.out::println);
//        System.out.println("---------------------------------------------------Удаление нормал №1");

        if (manager.deleteSubTaskById(3)) {
            System.out.println("Удаление прошло успешно!");
        }
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Удаление саб №3");

        if (manager.deleteEpicTaskById(2)) {
            System.out.println("Удаление прошло успешно!");
        }
        getAllTasks().forEach(System.out::println);
        System.out.println("---------------------------------------------------Удаление эпик №2");
        Random rnd = new Random();
        Task task;
        for (int i = 0; i < 30; i++) {
            switch (rnd.nextInt(3)){
                case 0:
                    task= manager.getByIdNormalTask(1);
                    break;
                case 1:
                    task = manager.getByIdEpicTask(6);
                    break;
                case 2:
                    task = manager.getByIdSubTask(7);
                    break;
            }
        }
        int count = 0;
        for (Task taskInHistory : manager.getHistory()) {

            System.out.println(count++ + " : " + taskInHistory);
        }

//        EpicTask epicTaskT_1 = manager.getByIdEpicTask(6);
//        SubTask subTaskT_1 = new SubTask("test1", New, epicTaskT_1.getId());
//        SubTask subTaskT_2 = new SubTask("test2", New, epicTaskT_1.getId());
//        manager.addSubTask(subTaskT_1);
//        manager.addSubTask(subTaskT_2);
//        //для тест id эпика меняется через дебагер, хотел через дебагер удалить саб из эпика, но не нашел как сделать
//        //Если не менять id будет проверка на изменение не существующего эпика
//        EpicTask epicTaskT_2 = new EpicTask("для теста обновления с другим количеством сабов");
//        manager.upgradeEpicTask(epicTaskT_2);
//        getAllTasks().forEach(System.out::println);


    }
}
