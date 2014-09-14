package ru.dokwork.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Client of {@link ru.dokwork.todo.TaskService}
 */
public class TaskServiceClient {

    private final String URL;
    private final JsonParser parser;

    public HttpResponse getTask(UUID uuid) throws IOException {
        String newUrl = URL + uuid.toString();
        return Request.Get(newUrl).execute().returnResponse();
    }

    public HttpResponse getAll() throws IOException {
        return Request.Get(URL)
                .execute().returnResponse();
    }

    public HttpResponse addNewTask(JsonObject jtask) throws IOException {
        return Request.Post(URL)
                .bodyString(jtask.toString(), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
    }

    public HttpResponse putTask(UUID uuid, JsonObject jtask) throws IOException {
        return Request.Put(URL+uuid)
                .bodyString(jtask.toString(), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
    }

    public HttpResponse delete(UUID uuid) throws IOException {
        return Request.Delete(URL + uuid.toString())
                .execute()
                .returnResponse();
    }

    public HttpResponse patch(UUID uuid, String name, String description, Boolean isCompleted) throws URISyntaxException, IOException {
        URIBuilder uri = new URIBuilder().setPath(URL+uuid);
        if (name != null) {
            uri.addParameter("name", name);
        }
        if (description != null) {
            uri.addParameter("description", description);
        }
        if (isCompleted != null) {
            uri.addParameter("completed", isCompleted.toString());
        }
        HttpPatch patch = new HttpPatch(uri.build());
        HttpClient httpclient = HttpClients.createDefault();
        return httpclient.execute(patch);
    }

    public int getTasksCount() throws IOException {
        return parser.parse(readResponseToSring(getAll())).getAsJsonArray().size();
    }

    public void removeAllExistingTasks() throws IOException {
        HttpResponse httpResponse = getAll();
        JsonArray array = parser.parse(readResponseToSring(httpResponse)).getAsJsonArray();
        for (JsonElement jsonElement : array) {
            JsonObject jtask = jsonElement.getAsJsonObject();
            delete(UUID.fromString(jtask.get("uuid").getAsString()));
        }
    }

    public TaskServiceClient(String url) {
        URL = url;
        parser = new JsonParser();
    }

    private static String readResponseToSring(HttpResponse response) throws IOException {
        java.util.Scanner s = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
