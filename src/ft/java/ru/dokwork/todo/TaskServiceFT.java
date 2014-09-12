package ru.dokwork.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class TaskServiceFT {

    private static final String URL = "http://localhost:8080/todo/tasks/";

    JsonParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new JsonParser();
    }

    @Test
    public void testAddNewTask() throws Exception {
        String name = "Name of the task";
        String description = "Description of the task";
        String jtask = new JsonBuilder()
                .addProperty("name", name)
                .addProperty("description", description)
                .toString();

        HttpResponse response = Request.Post(URL)
                .bodyString(jtask, ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();

        Assert.assertEquals("Wrong code after add new task by POST",
                201, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getHeaders("location"));
        Assert.assertEquals(1, response.getHeaders("location").length);
        System.out.println(response.getHeaders("location")[0]);
    }

    @Test
    public void testGetTask() throws Exception {
        String name = "Name of the task";
        String description = "Description of the task";
        JsonObject jtask = new JsonBuilder()
                .addProperty("name", name)
                .addProperty("description", description)
                .toJsonObject();
        HttpResponse response = Request.Post(URL)
                .bodyString(jtask.toString(), ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();

        String newUrl = response.getFirstHeader("location").getValue();
        String answer = Request.Get(newUrl).execute().returnContent().asString();

        Assert.assertFalse("Answer is empty.", answer.isEmpty());

        jtask = parser.parse(answer).getAsJsonObject();
        Assert.assertTrue("Answer have a wrong UUID", newUrl.endsWith(jtask.get("uuid").getAsString()));
        Assert.assertEquals("Answer have a wrong name", name, jtask.get("name").getAsString());
        Assert.assertEquals("Answer have a wrong description", description, jtask.get("description").getAsString());
        Assert.assertFalse("Answer have a wrong status", Boolean.getBoolean(jtask.get("completed").getAsString()));
    }

    @Test
    public void testGetAll() throws Exception {
        int N = 5;
        for (int i = 0; i < N; i++) {
            JsonObject jtask = new JsonBuilder()
                    .addProperty("name", UUID.randomUUID().toString())
                    .addProperty("description", UUID.randomUUID().toString())
                    .toJsonObject();
            Request.Post(URL)
                    .bodyString(jtask.toString(), ContentType.APPLICATION_JSON)
                    .execute();
        }
        String answer = Request.Get(URL)
                .execute()
                .returnContent().asString();
        JsonElement jsonElement = parser.parse(answer);
        Assert.assertTrue("Answer is not json array.", jsonElement.isJsonArray());
        Assert.assertTrue("Wrong count elements in answer.", jsonElement.getAsJsonArray().size() >= N);
    }

    @Test
    public void testDelete() throws IOException {
        JsonObject jtask = new JsonBuilder()
                .addProperty("name", UUID.randomUUID().toString())
                .addProperty("description", UUID.randomUUID().toString())
                .toJsonObject();
        Request.Post(URL)
                .bodyString(jtask.toString(), ContentType.APPLICATION_JSON)
                .execute();
        String allTasks = Request.Get(URL)
                .execute()
                .returnContent().asString();
        JsonArray elements = parser.parse(allTasks).getAsJsonArray();
        int countBefore = elements.size();

        jtask = elements.get(0).getAsJsonObject();
        HttpResponse response = Request.Delete(URL + jtask.get("uuid").getAsString())
                .execute()
                .returnResponse();

        allTasks = Request.Get(URL)
                .execute()
                .returnContent().asString();
        elements = parser.parse(allTasks).getAsJsonArray();
        int countAfter = elements.size();

        Assert.assertEquals("Wrong code.", 204, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Incorrect difference.", 1, countBefore - countAfter);
    }

    class JsonBuilder {
        JsonObject object;

        JsonBuilder addProperty(String name, String value) {
            object.addProperty(name, value);
            return this;
        }

        @Override
        public String toString() {
            return object.toString();
        }

        public JsonObject toJsonObject() {
            return object;
        }

        JsonBuilder() {
            this.object = new JsonObject();
        }
    }
}