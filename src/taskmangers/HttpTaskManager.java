package taskmangers;

import api.HttpTasksServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kvserver.KVServer;
import kvserver.KVTaskClient;
import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private String url;
    private KVServer kvServer;
    HttpTasksServer tasksServer;
    KVTaskClient kvTaskClient;
    Gson gson;

    public HttpTaskManager(String url) {
        this.url = url;
        gson = new Gson();
    }
    public void createKVClient(){
        kvTaskClient = new KVTaskClient(url);
    }

    public static void main(String[] args) {
        new HttpTaskManager("localhost").startServers();
    }

    public void startServers() {
        kvServer = new KVServer();
        kvServer.start();
        tasksServer = new HttpTasksServer(this);
        tasksServer.startServer();
        kvTaskClient = new KVTaskClient("localhost");
    }

    public void stopServers() {
        System.out.println("stop servers");
        kvServer.stop();
        tasksServer.stop();
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public void save() {
        super.save();
        kvTaskClient.put("task", gson.toJson(getAllNormalTasks(),new TypeToken<ArrayList<NormalTask>>() {
        }.getType()));
        kvTaskClient.put("subtask", gson.toJson(getAllSubTasks(),new TypeToken<ArrayList<SubTask>>() {
        }.getType()));
        kvTaskClient.put("epic", gson.toJson(getAllEpicTasks(),new TypeToken<ArrayList<EpicTask>>() {
        }.getType()));

        List<Integer> list = getHistory().stream().map(Task::getId).collect(Collectors.toList());
        kvTaskClient.put("history", gson.toJson(list,new TypeToken<ArrayList<Integer>>() {
        }.getType()));
        kvTaskClient.put("globalid", String.valueOf(globalId));
    }



    public void load() {
        HashMap<Integer,Task> allTasks = new HashMap<>();
        String task = kvTaskClient.load("task");
        List<NormalTask> deserializationNormalTask = gson.fromJson(task,
                new TypeToken<ArrayList<NormalTask>>() {
                }.getType());
        deserializationNormalTask.forEach(normalTask ->
        {
            normalTasks.put(normalTask.getId(), normalTask);
            allTasks.put(normalTask.getId(), normalTask);
        });

        List<SubTask> deserializationSubTask = gson.fromJson(kvTaskClient.load("subtask"),
                new TypeToken<ArrayList<SubTask>>() {
                }.getType());
        deserializationSubTask.forEach(subTask -> {
            subTasks.put(subTask.getId(), subTask);
            allTasks.put(subTask.getId(), subTask);
        });
        List<EpicTask> deserializationEpicTask = gson.fromJson(kvTaskClient.load("epic"),
                new TypeToken<ArrayList<EpicTask>>() {
                }.getType());
        deserializationEpicTask.forEach(epicTask ->{
            epicTasks.put(epicTask.getId(), epicTask);
            allTasks.put(epicTask.getId(), epicTask);
        }
        );
        List<Integer> deserializationHistory = gson.fromJson(kvTaskClient.load("history"),
                new TypeToken<ArrayList<Integer>>() {
                }.getType());
        for (Integer taskId : deserializationHistory) {
            history.add(allTasks.get(taskId));
        }
        prioritizedTasks.addAll(allTasks.values());
    }


}
