import storetasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path fileName;

    public FileBackedTasksManager() {
        fileName = Path.of("history.csv");
    }

    public void save() {
        String text = getText();
        try {
            Files.deleteIfExists(fileName);
            Files.write(fileName, text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка сохранения в файл!");
        }

    }

    public static FileBackedTasksManager loadFromFile(Path fileName) {
        FileBackedTasksManager manager = new FileBackedTasksManager();
        List<String> lines;
        try {
            lines = Files.readAllLines(fileName);
            manager.restoreInformation(manager, lines);
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка загрузки из файла!");
        }
        return manager;
    }

    public static void loadFromFile(File fileName) {
        loadFromFile(Path.of(fileName.toString()));
    }

    private void restoreInformation(FileBackedTasksManager manager, List<String> lines) {
        HashMap<Integer, Task> addedTasks = new HashMap<>();
        int currentLine = 0;
        int maxId = 0;
        while (currentLine < lines.size()) {
            String[] elementLine = lines.get(currentLine).split(",");
            LinesType type = LinesType.valueOf(elementLine[0]);
            int quantityLines = Integer.parseInt(elementLine[1]);
            switch (type) {
                case Normal:
                    currentLine += 2;
                    for (int i = 0; i < quantityLines; i++) {
                        String[] elementsLineNormal = lines.get(currentLine).split(",");
                        NormalTask normalTask = (NormalTask) manager.createTask(type, elementsLineNormal);
                        manager.normalTasks.put(normalTask.getId(), normalTask);
                        if (maxId < normalTask.getId()) {
                            maxId = normalTask.getId();
                        }
                        addedTasks.put(normalTask.getId(), normalTask);
                        currentLine++;
                    }
                    break;
                case Epic:
                    currentLine += 2;
                    for (int i = 0; i < quantityLines; i++) {
                        String[] elementsLineEpic = lines.get(currentLine).split(",");
                        EpicTask epicTask = (EpicTask) manager.createTask(type, elementsLineEpic);
                        if (elementsLineEpic.length > 4) {
                            for (int subId = 4; subId < elementsLineEpic.length; subId++) {
                                epicTask.addSubTaskInList(Integer.parseInt(elementsLineEpic[subId]));
                            }

                        }
                        manager.epicTasks.put(epicTask.getId(), epicTask);
                        if (maxId < epicTask.getId()) {
                            maxId = epicTask.getId();
                        }
                        addedTasks.put(epicTask.getId(), epicTask);
                        currentLine++;
                    }
                    break;
                case Sub:
                    currentLine += 2;
                    for (int i = 0; i < quantityLines; i++) {
                        String[] elementsLineSub = lines.get(currentLine).split(",");
                        SubTask subTask = (SubTask) manager.createTask(type, elementsLineSub);
                        subTask.setEpicTaskId(Integer.parseInt(elementsLineSub[4]));
                        manager.subTasks.put(subTask.getId(), subTask);
                        if (maxId < subTask.getId()) {
                            maxId = subTask.getId();
                        }
                        addedTasks.put(subTask.getId(), subTask);
                        currentLine++;
                    }
                    break;
                case History:
                    currentLine += 2;
                    String[] elementsLineHistory = lines.get(currentLine).split(",");
                    for (int i = 0; i < Integer.parseInt(elementLine[1]); i++) {
                        int id = Integer.parseInt(elementsLineHistory[i]);
                        manager.history.add(addedTasks.get(id));
                    }
                    currentLine++;
                    break;
            }
        }
        manager.globalId = ++maxId;
    }

    private Task createTask(LinesType type, String[] elementsLine) {
        int id = Integer.parseInt(elementsLine[0]);
        String title = elementsLine[1];
        String description = elementsLine[2];
        StatusTask status = StatusTask.valueOf(elementsLine[3]);
        Task task;
        if (type == LinesType.Normal) {
            task = new NormalTask(title, description, status);
        } else if (type == LinesType.Epic) {
            task = new EpicTask(title, description, status);
        } else {
            task = new SubTask(title, description, status);
        }
        task.setId(id);
        return task;
    }

    private String getText() {
        StringBuilder sb = new StringBuilder();
        sb.append(LinesType.Normal)
                .append(",")
                .append(normalTasks.size())
                .append("\n")
                .append("id,title,description,status\n");
        for (NormalTask task : normalTasks.values()) {
            sb.append(task.getId()).append(",")
                    .append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getStatus()).append("\n");
        }
        sb.append(LinesType.Epic)
                .append(",")
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
        sb.append(LinesType.Sub)
                .append(",")
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
        sb.append(LinesType.History)
                .append(",")
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

    @Override
    public boolean deleteNormalTaskById(int id) {
        boolean value = super.deleteNormalTaskById(id);
        if (value) {
            save();
        }
        return value;
    }

    @Override
    public boolean deleteEpicTaskById(int id) {
        boolean value = super.deleteEpicTaskById(id);
        if (value) {
            save();
        }
        return value;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        boolean value = super.deleteSubTaskById(id);
        if (value) {
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
        EpicTask epicTask = super.getByIdEpicTask(id);
        save();
        return epicTask;
    }

    @Override
    public SubTask getByIdSubTask(int id) {
        SubTask subTask = super.getByIdSubTask(id);
        save();
        return subTask;
    }
}


