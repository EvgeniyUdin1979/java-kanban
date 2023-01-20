package taskmangers;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

public class HttpTaskManager extends FileBackedTasksManager{
    private final URI uri;
    private final HttpClient client;
    Gson gson;
    public HttpTaskManager(String url) throws MalformedURLException, URISyntaxException {
        this.uri = new URL(url).toURI();
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    @Override
    public void save() throws URISyntaxException {
        String text = gson.toJson(getText(),String.class);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(text, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();
    }
}
