package ru.dokwork.todo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
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

    public TaskServiceClient(String url) {
        URL = url;
    }
}
