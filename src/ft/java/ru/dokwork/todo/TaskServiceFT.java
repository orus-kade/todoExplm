package ru.dokwork.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class TaskServiceFT {

    private static final String URL = "http://localhost:8080/todo/tasks/";

    static final JsonObject jtask1;

    static final JsonObject jtask2;

    JsonParser parser;

    TaskServiceClient todo;

    static {
        jtask1 = new JsonObject();
        jtask1.addProperty("name", "First task");
        jtask1.addProperty("description", "First task for test.");

        jtask2 = new JsonObject();
        jtask2.addProperty("name", "Second task");
        jtask2.addProperty("description", "Second task for test.");
    }

    @Before
    public void setup() throws Exception {
        parser = new JsonParser();
        todo = new TaskServiceClient(URL);
        removeAllExistingTasks();
    }

    @Test
    public void functionalTest() throws Exception {
        // Add first task
        HttpResponse response = todo.addNewTask(jtask1);
        // Check answer
        ResponseChecker.assertThat(response).haveCode(201).haveHeaders("location", 1);
        String uuid1 = response.getFirstHeader("location").getValue().substring(URL.length());

        // Get first task
        response = todo.getTask(UUID.fromString(uuid1));
        // Create expected json
        JsonObject expectedObject = createJsonTask(uuid1, jtask1);
        // Check answer
        ResponseChecker.assertThat(response).haveCode(200).haveContent(expectedObject);

        // Add second task
        response = todo.addNewTask(jtask2);
        // Check answer
        ResponseChecker.assertThat(response).haveCode(201).haveHeaders("location", 1);
        String uuid2 = response.getFirstHeader("location").getValue().substring(URL.length());

        // Get all tasks
        response = todo.getAll();
        // Expected array
        JsonArray array = new JsonArray();
        array.add(createJsonTask(uuid1, jtask1));
        array.add(createJsonTask(uuid2, jtask2));
        // Check answer
        ResponseChecker.assertThat(response).haveCode(200).haveContent(array);

        // Delete task
        int countBefore = getTasksCount();
        response = todo.delete(UUID.fromString(uuid2));
        // Check answer
        ResponseChecker.assertThat(response).haveCode(204);
        int countAfter = getTasksCount();
        Assert.assertEquals(1, countBefore - countAfter);
    }

    private int getTasksCount() throws Exception {
        return parser.parse(readResponseToSring(todo.getAll())).getAsJsonArray().size();
    }

    private void removeAllExistingTasks() throws Exception {
        HttpResponse httpResponse = todo.getAll();
        JsonArray array = parser.parse(readResponseToSring(httpResponse)).getAsJsonArray();
        for (JsonElement jsonElement : array) {
            JsonObject jtask = jsonElement.getAsJsonObject();
            todo.delete(UUID.fromString(jtask.get("uuid").getAsString()));
        }
        array = parser.parse(readResponseToSring(todo.getAll())).getAsJsonArray();
        Assert.assertEquals(0, array.size());
    }

    private static String readResponseToSring(HttpResponse response) throws IOException {
        java.util.Scanner s = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private JsonObject createJsonTask(String uuid, JsonObject pattern) {
        JsonObject expectedObject = new JsonObject();
        expectedObject.add("uuid", parser.parse(uuid));
        expectedObject.add("name", pattern.get("name"));
        expectedObject.add("description", pattern.get("description"));
        expectedObject.add("completed", parser.parse("false"));
        return expectedObject;
    }
}