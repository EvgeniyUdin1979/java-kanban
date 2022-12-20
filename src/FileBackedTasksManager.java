import storetasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path fileName;

    public FileBackedTasksManager() {
        fileName = Path.of("history.csv");
    }

    public void save() {
        String text = getText();
        try {
            Files.deleteIfExists(fileName);
            Files.write(fileName,text.getBytes(StandardCharsets.UTF_8));
        }catch (IOException ex){
            throw new ManagerSaveException("Ошибка сохранения в файл!");
        }

    }
    public static FileBackedTasksManager loadFromFile(Path fileName){
        FileBackedTasksManager manager = new FileBackedTasksManager();
        Queue<String> lines;
        try {
            lines = new ArrayDeque<>(Files.readAllLines(fileName));
            manager.restoreInformation(manager,lines);
        }catch (IOException ex){
          throw new ManagerSaveException("Ошибка загрузки из файла!");
        }
        return manager;
    }

    public static void loadFromFile(File fileName){
        loadFromFile(Path.of(fileName.toString()));
    }

    private void restoreInformation(FileBackedTasksManager manager, Queue<String> lines){
        while (!lines.isEmpty()){
            String [] elementLine = lines.poll().split(",");
            String type = elementLine[0];
            switch (type){
                case "normal":
                    lines.remove();
                    for (int i = 0; i < Integer.parseInt(elementLine[1]); i++) {
                        String [] elementsLineNormal = lines.poll().split(",");
                        NormalTask normalTask =(NormalTask) manager.createTask(type,elementsLineNormal);
                        manager.normalTasks.put(normalTask.getId(),normalTask);
                    }
                    break;
                case "epic":
                    lines.remove();
                    for (int i = 0; i < Integer.parseInt(elementLine[1]); i++) {
                        String [] elementsLineEpic = lines.poll().split(",");
                        EpicTask epicTask = (EpicTask) manager.createTask(type,elementsLineEpic);
                        if (elementsLineEpic.length > 4){
                            for (int subId = 4; subId < elementsLineEpic.length; subId++) {
                                epicTask.addSubTaskInList(Integer.parseInt(elementsLineEpic[subId]));
                            }
                        }
                        manager.epicTasks.put(epicTask.getId(),epicTask);
                    }
                    break;
                case "sub":
                    lines.remove();
                    for (int i = 0; i < Integer.parseInt(elementLine[1]); i++) {
                        String [] elementsLineSub = lines.poll().split(",");
                        SubTask subTask = (SubTask) manager.createTask(type,elementsLineSub);
                        subTask.setEpicTaskId(Integer.parseInt(elementsLineSub[4]));
                        manager.subTasks.put(subTask.getId(),subTask);
                    }
                    break;
                case "history":
                    lines.remove();
                    String [] elementsLineHistory = lines.poll().split(",");
                    for (int i = 0; i < Integer.parseInt(elementLine[1]); i++) {
                        int id = Integer.parseInt(elementsLineHistory[i]);
                        manager.history.add(manager.taskByIdForHistory(manager,id));
                    }
            }
        }
    }

    private Task taskByIdForHistory(FileBackedTasksManager manager, int id){
        if (manager.normalTasks.containsKey(id)){
            return manager.normalTasks.get(id);
        }else if (manager.epicTasks.containsKey(id)){
            return manager.epicTasks.get(id);
        }else if (manager.subTasks.containsKey(id)){
            return manager.subTasks.get(id);
        }else {
            return null;
        }
    }

    private String getText() {
        StringBuilder sb = new StringBuilder();
        sb.append("normal,")
                .append(normalTasks.size())
                .append("\n")
                .append("id,title,description,status\n");
        for (NormalTask task : normalTasks.values()) {
            sb.append(task.getId()).append(",")
                    .append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getStatus()).append("\n");
        }
        sb.append("epic,")
                .append(epicTasks.size())
                .append("\n")
                .append("id,title,description,status,subId\n");
        for (EpicTask task : epicTasks.values()) {
            sb.append(task.getId()).append(",")
                    .append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getStatus());
            if (!task.getSubTasks().isEmpty()) {
                sb.append(",");
                task.getSubTasks().forEach(subId -> {
                    sb.append(subId).append(",");
                });
                sb.setLength(sb.length() - 1);
            }
            sb.append("\n");
        }
        sb.append("sub,")
                .append(subTasks.size())
                .append("\n")
                .append("id,title,description,status,epicId\n");
        for (SubTask task : subTasks.values()) {
            sb.append(task.getId()).append(",")
                    .append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getStatus()).append(",")
                    .append(task.getEpicTaskId()).append("\n");
        }
        sb.append("history,")
                .append(history.size())
                .append("\n")
                .append("id\n");
        if (history.size() > 0) {
            history.getHistory().forEach(task -> {
                sb.append(task.getId()).append(",");
            });
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private Task createTask(String type,String [] elementsLine){
        int id = Integer.parseInt(elementsLine[0]);
        String title = elementsLine[1];
        String description = elementsLine[2];
        StatusTask status = StatusTask.valueOf(elementsLine[3]);
        Task task;
        if (type.equals("normal")){
            task = new NormalTask(title,description,status);
        }else if (type.equals("epic")){
            task = new EpicTask(title,description,status);
        }else if (type.equals("sub")){
            task = new SubTask(title,description,status);
        }else {
            throw new RuntimeException();
        }
        task.setId(id);
        return task;
    }

    @Override
    public boolean deleteNormalTaskById(int id) {
        boolean value = super.deleteNormalTaskById(id);
        if (value){
            save();
        }
        return value;
    }

    @Override
    public boolean deleteEpicTaskById(int id) {
        boolean value = super.deleteEpicTaskById(id);
        if (value){
            save();
        }
        return value;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        boolean value = super.deleteSubTaskById(id);
        if (value){
            save();
        }
        return value;
    }

    @Override
    public void addNormalTask(NormalTask task) {
        super.addNormalTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask task) {
        super.addEpicTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask task) {
        super.addSubTask(task);
        save();
    }

    @Override
    public void upgradeNormalTask(NormalTask task) {
        super.upgradeNormalTask(task);
        save();
    }

    @Override
    public void upgradeSubTask(SubTask task) {
        super.upgradeSubTask(task);
        save();
    }

    @Override
    public void upgradeEpicTask(EpicTask task) {
        super.upgradeEpicTask(task);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public NormalTask getByIdNormalTask(int id) {
        NormalTask normalTask = super.getByIdNormalTask(id);
        save();
        return normalTask;
    }

    @Override
    public EpicTask getByIdEpicTask(int id) {
        EpicTask epicTask =  super.getByIdEpicTask(id);
        save();
        return epicTask;
    }

    @Override
    public SubTask getByIdSubTask(int id) {
        SubTask subTask =  super.getByIdSubTask(id);
        save();
        return subTask;
    }
}


