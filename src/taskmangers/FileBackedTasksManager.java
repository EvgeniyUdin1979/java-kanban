package taskmangers;

import storetasks.*;
import taskmangers.erros.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    protected void restoreInformation(FileBackedTasksManager manager, List<String> lines) {
        HashMap<Integer, Task> addedTasks = new HashMap<>();
        int currentLine = 0;
        int maxId = 0;
        while (currentLine < lines.size()) {
            String[] elementLine = lines.get(currentLine).split(",");
            if (elementLine.length != 2){
               throw new RuntimeException("Не верное число элементов в строке Type");
            }
            LinesType type = LinesType.valueOf(elementLine[0]);
            int quantityLines = Integer.parseInt(elementLine[1]);
            if (quantityLines == 0) {
                currentLine += 2;
                continue;
            }
            currentLine += 2;
            if (currentLine >= lines.size()) {
                continue;
            }
            switch (type) {
                case Normal:
                    for (int i = 0; i < quantityLines; i++) {
                        String[] elementsLineNormal = lines.get(currentLine).split(",");
                        if (elementsLineNormal.length != 6) {
                            throw new RuntimeException("Не верное число элементов в строке Нормал тасков");
                        }
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
                    for (int i = 0; i < quantityLines; i++) {
                        String[] elementsLineEpic = lines.get(currentLine).split(",");
                        if (elementsLineEpic.length < 6) {
                            throw new RuntimeException("Не верное число элементов в строке Эпик тасков");
                        }
                        EpicTask epicTask = (EpicTask) manager.createTask(type, elementsLineEpic);
                        if (elementsLineEpic.length > 6) {
                            for (int subId = 6; subId < elementsLineEpic.length; subId++) {
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
                    for (int i = 0; i < quantityLines; i++) {
                        String[] elementsLineSub = lines.get(currentLine).split(",");
                        if (elementsLineSub.length != 7) {
                            throw new RuntimeException("Не верное число элементов в строке Саб тасков");
                        }
                        SubTask subTask = (SubTask) manager.createTask(type, elementsLineSub);
                        subTask.setEpicTaskId(Integer.parseInt(elementsLineSub[6]));
                        manager.subTasks.put(subTask.getId(), subTask);
                        if (maxId < subTask.getId()) {
                            maxId = subTask.getId();
                        }
                        addedTasks.put(subTask.getId(), subTask);
                        currentLine++;
                    }
                    break;
                case History:
                    int quantityElements = Integer.parseInt(elementLine[1]);
                    String[] elementsLineHistory = lines.get(currentLine).split(",");
                        if (elementsLineHistory.length != quantityElements) {
                            throw new RuntimeException("Не верное число элементов в строке History");
                        }
                    for (int i = 0; i < quantityElements; i++) {
                        int id = Integer.parseInt(elementsLineHistory[i]);
                        if (!addedTasks.containsKey(id)) {
                            throw new RuntimeException("Таск " + id + " не был восстановлен из файла!");
                        }
                        manager.history.add(addedTasks.get(id));
                    }
                    currentLine++;
                    break;
            }
        }
        List<EpicTask> epicTasks = new ArrayList<>();
        for (Task task : addedTasks.values()) {
            prioritizedTasks.add(task);
            if (task instanceof EpicTask){
                epicTasks.add((EpicTask) task);
            }
        }
        epicTasks.forEach(this::changeEpicTaskPriority);
        manager.globalId = ++maxId;
    }

    private Task createTask(LinesType type, String[] elementsLine) {
        int id = Integer.parseInt(elementsLine[0]);
        String title = elementsLine[1];
        String description = elementsLine[2];
        StatusTask status = StatusTask.valueOf(elementsLine[3]);
        LocalDateTime startTime = getLocalDateTimeFromString(elementsLine[4]);
        long duration = Long.parseLong(elementsLine[5]);
        Task task;
        if (type == LinesType.Normal) {
            task = new NormalTask(title, description, status, startTime, duration);
        } else if (type == LinesType.Epic) {
            task = new EpicTask(title, description, status, startTime, duration);
        } else {
            task = new SubTask(title, description, status, startTime, duration);
        }
        task.setId(id);
        return task;
    }

    protected String getText() {
        StringBuilder sb = new StringBuilder();
        getTextFromNormalTasks(sb);
        getTextFromEpicTasks(sb);
        getTextFromSubTasks(sb);
        getTextFromHistory(sb);
        return sb.toString();
    }

    private void getTextFromHistory(StringBuilder sb) {
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
    }

    private void getTextFromSubTasks(StringBuilder sb) {
        sb.append(LinesType.Sub)
                .append(",")
                .append(subTasks.size())
                .append("\n")
                .append("id,title,description,status,startTime,duration,epicId\n");
        for (SubTask task : subTasks.values()) {
            sb.append(task.getId()).append(",")
                    .append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getStatus()).append(",")
                    .append(getStringFromStartTime(task.getStartTime())).append(",")
                    .append(task.getDuration()).append(",")
                    .append(task.getEpicTaskId()).append("\n");
        }
    }

    private void getTextFromEpicTasks(StringBuilder sb) {
        sb.append(LinesType.Epic)
                .append(",")
                .append(epicTasks.size())
                .append("\n")
                .append("id,title,description,status,startTime,duration,subId\n");
        for (EpicTask task : epicTasks.values()) {
            sb.append(task.getId()).append(",")
                    .append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getStatus()).append(",")
                    .append(getStringFromStartTime(task.getStartTime())).append(",")
                    .append(task.getDuration());
            if (!task.getSubTasks().isEmpty()) {
                sb.append(",");
                task.getSubTasks().forEach(subId -> {
                    sb.append(subId).append(",");
                });
                sb.setLength(sb.length() - 1);
            }
            sb.append("\n");
        }
    }

    private void getTextFromNormalTasks(StringBuilder sb) {
        sb.append(LinesType.Normal)
                .append(",")
                .append(normalTasks.size())
                .append("\n")
                .append("id,title,description,status,startTime,duration\n");
        for (NormalTask task : normalTasks.values()) {
            sb.append(task.getId()).append(",")
                    .append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getStatus()).append(",")
                    .append(getStringFromStartTime(task.getStartTime())).append(",")
                    .append(task.getDuration()).append("\n");
        }
    }

    private String getStringFromStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            return "null";
        } else {
            return String.valueOf(startTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        }
    }

    private LocalDateTime getLocalDateTimeFromString(String startTime) {
        if (startTime.equals("null")) {
            return null;
        } else {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(startTime)), ZoneOffset.UTC);
        }
    }

    @Override
    public void deleteNormalTaskById(int id) {
        super.deleteNormalTaskById(id);
        save();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
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


