package ru.dokwork.todo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.util.UUID;

import static org.junit.Assert.*;

public class TaskServiceFT {

    private static final String URL = "http://localhost:8080/todo/tasks/";

    Gson gson;
    JsonParser parser;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
        parser = new JsonParser();
    }

    @Test
    public void testGet() throws Exception {
        String name = "Name of the task";
        String description = "Description of the task";
        String jtask = new JsonBuilder()
                .addProperty("name", name)
                .addProperty("description", description)
                .toString();

        HttpResponse response = Request.Post(URL + "add")
                .bodyString(jtask, ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();

        Assert.assertEquals("Wrong code after add new task by POST",
                201, response.getStatusLine().getStatusCode());
        Assert.assertNotNull(response.getHeaders("location"));
        Assert.assertEquals(1, response.getHeaders("location").length);
        System.out.println(response.getHeaders("location")[0]);
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

        JsonBuilder() {
            this.object = new JsonObject();
        }
    }
}