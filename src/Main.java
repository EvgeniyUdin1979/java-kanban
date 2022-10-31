import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;

import static storetasks.StatusTask.*;

public class Main {
    static Manager manager = new Manager();

    static {
        NormalTask normalTask = new NormalTask(manager.getGlobalId(), "firstNormal", New);
        manager.addNormalTask(normalTask);
        EpicTask epicTask = new EpicTask(manager.getGlobalId(), "firstEpic");
        manager.addEpicTask(epicTask);
        SubTask subTask1 = new SubTask(manager.getGlobalId(), "firstSub1", New, epicTask);
        SubTask subTask2 = new SubTask(manager.getGlobalId(), "firstSub2", New, epicTask);
        SubTask subTask3 = new SubTask(manager.getGlobalId(), "firstSub3", New, epicTask);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
        EpicTask epicTask2 = new EpicTask(manager.getGlobalId(), "secondEpic");
        manager.addEpicTask(epicTask2);
        SubTask subTask4 = new SubTask(manager.getGlobalId(), "firstSub4", New, epicTask2);
        manager.addSubTask(subTask4);
    }

    public static void main(String[] args) {
        manager.getAllTask().forEach(System.out::println);
        NormalTask normal1 = manager.getByIdNormalTask(0);
        normal1.setStatus(In_progress);
        normal1.setDescription("описание");
        manager.upgradeNormalTask(normal1);
        System.out.println("---------------------------------------------------");

        SubTask subTask1 = manager.getByIdSubTask(2);
        subTask1.setStatus(New);
        manager.upgradeSubTask(subTask1);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        SubTask subTask2 = manager.getByIdSubTask(3);
        subTask2.setStatus(Done);
        manager.upgradeSubTask(subTask2);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        SubTask subTask3 = manager.getByIdSubTask(4);
        subTask3.setStatus(Done);
        manager.upgradeSubTask(subTask3);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        manager.deleteByIdNormalTask(0);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        manager.deleteByIdSubTask(2);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        manager.deleteByIdEpicTask(1);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        EpicTask epic = manager.getByIdEpicTask(5);
        epic.setStatus(Done);
        manager.getAllTask().forEach(System.out::println);


    }
}
