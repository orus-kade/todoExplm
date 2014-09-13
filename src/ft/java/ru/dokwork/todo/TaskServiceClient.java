package ru.dokwork.todo;

import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Client of {@link ru.dokwork.todo.TaskService}
 */
public class TaskServiceClient {

    private final String URL;

    public HttpResponse addNewTask(JsonObject jtask) throws Exception {
        return Request.Post(URL)
                .bodyString(jtask.toString(), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
    }

    public HttpResponse getTask(UUID uuid) throws Exception {
        String newUrl = URL + uuid.toString();
        return Request.Get(newUrl).execute().returnResponse();
    }

    public HttpResponse getAll() throws Exception {
        return Request.Get(URL)
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

    public TaskServiceClient(String url) {
        URL = url;
    }
}
