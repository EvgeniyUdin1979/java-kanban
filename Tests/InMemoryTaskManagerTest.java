import taskmangers.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest {

    public  InMemoryTaskManagerTest(){
        super.setManager(new InMemoryTaskManager());
    }


}