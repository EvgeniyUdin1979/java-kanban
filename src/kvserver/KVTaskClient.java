package kvserver;

import com.google.gson.Gson;
import taskmangers.Managers;
import taskmangers.erros.HttpManagerConnectException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private String API_TOKEN;
    private final int PORT = 8078;
    private final String url;
    HttpClient client;
    Gson gson = Managers.getDefaultGson();

    public KVTaskClient(String url) throws HttpManagerConnectException{
        this.url = url;
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        register();
    }
    private void register() throws HttpManagerConnectException{
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/register");
            HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(),
                    HttpResponse.BodyHandlers.ofString());
            this.API_TOKEN = response.body();
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }
    }

    public void clear()throws HttpManagerConnectException{
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/clear/"+ "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder(uri).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }
    }
    public void put(String key, String json) throws HttpManagerConnectException{
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }
    }

    public String load(String key){
        try {
            URI uri = new URI("http://" + url + ":" + PORT + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpManagerConnectException("Проблемы во время соединения с KVServer.");
        }
    }
}
