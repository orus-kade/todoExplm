package ru.dokwork.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static ru.dokwork.todo.ResponseChecker.assertThat;

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
        jtask1.addProperty("completed", false);

        jtask2 = new JsonObject();
        jtask2.addProperty("name", "Second task");
        jtask2.addProperty("description", "Second task for test.");
        jtask2.addProperty("completed", false);
    }

    @Before
    public void setup() throws Exception {
        parser = new JsonParser();
        todo = new TaskServiceClient(URL);
        todo.removeAllExistingTasks();
    }

    @Test
    public void testGetTask() throws Exception {
        // Add task
        HttpResponse response = todo.addNewTask(jtask1);
        // Get uuid
        String uuid = response.getFirstHeader("location").getValue().substring(URL.length());
        // Get task
        response = todo.getTask(UUID.fromString(uuid));
        // Create expected json
        JsonObject expectedObject = createJsonTask(uuid, jtask1);
        // Check answer
        assertThat(response).haveCode(200).haveContent(expectedObject);
    }

    @Test
    public void testGetAllTasks() throws Exception {
        // Add first task
        HttpResponse response = todo.addNewTask(jtask1);
        String uuid1 = response.getFirstHeader("location").getValue().substring(URL.length());
        // Add second task
        response = todo.addNewTask(jtask2);
        String uuid2 = response.getFirstHeader("location").getValue().substring(URL.length());
        // Get all tasks
        response = todo.getAll();
        // Create expected array
        JsonArray array = new JsonArray();
        array.add(createJsonTask(uuid1, jtask1));
        array.add(createJsonTask(uuid2, jtask2));
        // Check answer
        assertThat(response).haveCode(200).haveContent(array);
    }

    @Test
    public void testAddTask() throws Exception {
        // Add task
        HttpResponse response = todo.addNewTask(jtask1);
        // Check answer
        assertThat(response).haveCode(201).haveHeaders("location", 1);
        // Get uuid
        String uuid = response.getFirstHeader("location").getValue().substring(URL.length());
        // Check new task
        response = todo.getTask(UUID.fromString(uuid));
        JsonObject addedTask = createJsonTask(uuid, jtask1);
        assertThat(response).haveContent(addedTask);
    }

    @Test
    public void testPutNewTask() throws Exception {
        // Put new task
        UUID uuid = UUID.randomUUID();
        HttpResponse response = todo.putTask(uuid, jtask1);
        // Check answer
        assertThat(response).haveCode(201).haveHeaders("location", 1);
        response.getFirstHeader("location").getValue().endsWith(uuid.toString());
        // Check new task
        response = todo.getTask(uuid);
        JsonObject addedTask = createJsonTask(uuid.toString(), jtask1);
        assertThat(response).haveContent(addedTask);
    }

    @Test
    public void testPutExistingTask() throws Exception {
        // Add task
        HttpResponse response = todo.addNewTask(jtask1);
        // Get uuid
        String uuid = response.getFirstHeader("location").getValue().substring(URL.length());
        // Change task
        JsonObject editedTask = createJsonTask(uuid, jtask1);
        editedTask.addProperty("name", "Edited task");
        // Put edited task
        response = todo.putTask(UUID.fromString(uuid), editedTask);
        // Check answer
        assertThat(response).haveCode(204).haveHeaders("location", 1);
        response.getFirstHeader("location").getValue().endsWith(uuid.toString());
        // Check edited task
        response = todo.getTask(UUID.fromString(uuid));
        assertThat(response).haveContent(editedTask);
    }

    @Test
    public void testPatchTask() throws Exception {
        // Add task
        HttpResponse response = todo.addNewTask(jtask1);
        // Get uuid
        String uuid = response.getFirstHeader("location").getValue().substring(URL.length());
        // Patch state for task
        response = todo.patch(UUID.fromString(uuid), null, null, true);
        // Check answer
        assertThat(response).haveCode(204).haveHeaders("location", 1);
        response.getFirstHeader("location").getValue().endsWith(uuid);
        // Check patched task
        response = todo.getTask(UUID.fromString(uuid));
        // Create expected json
        JsonObject expectedObject = createJsonTask(uuid, jtask1);
        expectedObject.addProperty("completed", true);
        assertThat(response).haveContent(expectedObject);
    }

    @Test
    public void testDeleteTask() throws Exception {
        // Add task
        HttpResponse response = todo.addNewTask(jtask1);
        // Get uuid
        String uuid = response.getFirstHeader("location").getValue().substring(URL.length());
        // Delete task
        int countBefore = todo.getTasksCount();
        response = todo.delete(UUID.fromString(uuid));
        // Check answer
        assertThat(response).haveCode(204);
        int countAfter = todo.getTasksCount();
        Assert.assertEquals(1, countBefore - countAfter);
    }

    private JsonObject createJsonTask(String uuid, JsonObject pattern) {
        JsonObject expectedObject = new JsonObject();
        expectedObject.add("uuid", parser.parse(uuid));
        expectedObject.add("name", pattern.get("name"));
        expectedObject.add("description", pattern.get("description"));
        expectedObject.add("completed", pattern.get("completed"));
        return expectedObject;
    }
}