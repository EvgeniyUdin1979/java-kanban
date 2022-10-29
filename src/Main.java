import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

public class Main {
    static Manager manager = new Manager();
    static {
        NormalTask normalTask = new NormalTask("firstNormal", 0);
        manager.addTask(normalTask);
        EpicTask epicTask = new EpicTask("firstEpic");
        manager.addTask(epicTask);
        SubTask subTask1 = new SubTask("firstSub1",0,epicTask);
        SubTask subTask2 = new SubTask("firstSub2",0,epicTask);
        SubTask subTask3 = new SubTask("firstSub3",0,epicTask);
        manager.addTask(subTask1);
        manager.addTask(subTask2);
        manager.addTask(subTask3);
        EpicTask epicTask2 = new EpicTask("secondEpic");
        manager.addTask(epicTask2);
        SubTask subTask4 = new SubTask("firstSub4",2,epicTask2);
        manager.addTask(subTask4);
    }

    public static void main(String[] args) {
        manager.getAllTask().forEach(System.out::println);
        Task normal1 = manager.getById(0);
        normal1.setStatus(1);
        normal1.setDescription("описание");
        manager.upgradeTask(normal1);
        System.out.println("---------------------------------------------------");

        Task subTask1 = manager.getById(2);
        subTask1.setStatus(0);
        manager.upgradeTask(subTask1);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        Task subTask2 = manager.getById(3);
        subTask2.setStatus(2);
        manager.upgradeTask(subTask2);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        Task subTask3 = manager.getById(4);
        subTask3.setStatus(2);
        manager.upgradeTask(subTask3);
        manager.getAllTask().forEach(System.out::println);

        System.out.println("---------------------------------------------------");
        Task epic = manager.getById(5);
        epic.setStatus(2);
        manager.upgradeTask(epic);
        manager.getAllTask().forEach(System.out::println);



    }
}
