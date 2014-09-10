package ru.dokwork.todo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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