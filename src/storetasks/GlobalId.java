package storetasks;

public class GlobalId {
    private static int globalId = 0;

    public static int getGlobalId(){

        return globalId++;
    }
}
