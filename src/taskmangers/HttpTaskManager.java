package taskmangers;

import api.HttpTasksServer;
import com.google.gson.Gson;
import kvserver.KVServer;
import taskmangers.erros.HttpManagerConnectException;
import taskmangers.erros.ManagerLoadException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private String url;
    private final HttpClient client;
    private String key;
    private String API_TOKEN;
    private final int PORT;
    private KVServer kvServer;
    HttpTasksServer tasksServer;
    Gson gson;

    public HttpTaskManager(String url) {
        this.url = url;
        PORT = 8078;
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        gson = new Gson();
        key = "first";
    }

    public static void main(String[] args) {
        new HttpTaskManager("localhost").startServers();
    }
    public void startServers(){
//        System.out.println("start");
            kvServer = new KVServer();
            kvServer.start();
            tasksServer = new HttpTasksServer(this);
            tasksServer.startServer();
            register();
    }
    public void stopServers(){
        System.out.println("stop servers");
        kvServer.stop();
        tasksServer.stop();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static HttpTaskManager loadFromServer(String url) {
        HttpTaskManager manager = new HttpTaskManager(url);
        String history = manager.load();
        if (history != null) {
            manager.restoreInformation(manager, List.of(history.split("\\r\\n|\\n")));
        }
        return manager;
    }

    @Override
    public void save() {
        String text = gson.toJson(getText(), String.class);
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(text, StandardCharsets.UTF_8))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            stopServers();
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/clear/"+ "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | InterruptedException | IOException e) {
            stopServers();
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }
    }

    public void register() {
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/register");
            HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(),
                    HttpResponse.BodyHandlers.ofString());
            this.API_TOKEN = response.body();
        } catch (IOException | URISyntaxException | InterruptedException e) {
            stopServers();
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }
    }

    public String load() {
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int lengthContent = Integer.parseInt(response.headers().firstValue("Content-length").orElse("0"));
            String body = null;
            if (lengthContent > 0) {
                body = gson.fromJson(response.body(), String.class);
            } else {
                try {
                    body = Files.readString(Path.of("history.csv"));
                } catch (IOException ex) {
                    throw new ManagerLoadException("Ошибка загрузки из файла!");
                }

            }
            return body;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            stopServers();
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }

    }


}
