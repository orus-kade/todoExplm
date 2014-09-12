package ru.dokwork.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

public class TaskServiceFT {

    private static final String URL = "http://localhost:8080/todo/tasks/";

    static final JsonParser parser;

    static final JsonObject jtask1;

    static final JsonObject jtask2;

    static {
        parser = new JsonParser();

        jtask1 = new JsonObject();
        jtask1.addProperty("name", "First task");
        jtask1.addProperty("description", "First task for test.");

        jtask2 = new JsonObject();
        jtask2.addProperty("name", "Second task");
        jtask2.addProperty("description", "Second task for test.");
    }

    @Test
    public void functionalTest() throws Exception {
        // Add first task
        Response response = addNewTask(jtask1);

        // Check answer
        ResponseChecker.assertFor(response).codeEqual(201).haveHeader("location", 1);

//        // Get first task
//        UUID uuid = UUID.fromString(httpResponse.getFirstHeader("location").getValue().substring(URL.length()));
//        String answer = getTask(uuid).returnContent().asString();
//
//        // Check answer
//        Assert.assertFalse("Answer is empty.", answer.isEmpty());
//
//        JsonObject jtask = parser.parse(answer).getAsJsonObject();
//        Assert.assertEquals("Answer have a wrong UUID", uuid.toString(), jtask.get("uuid").getAsString());
//        Assert.assertEquals("Answer have a wrong name", jtask1.get("name"), jtask.get("name"));
//        Assert.assertEquals("Answer have a wrong description", jtask1.get("description"), jtask.get("description"));
//        Assert.assertFalse("Answer have a wrong status", Boolean.getBoolean(jtask.get("completed").getAsString()));
    }

    public Response addNewTask(JsonObject jtask) throws Exception {
        return Request.Post(URL)
                .bodyString(jtask.toString(), ContentType.APPLICATION_JSON)
                .execute();
    }

    public Response getTask(UUID uuid) throws Exception {
        String newUrl = URL + uuid.toString();
        return Request.Get(newUrl).execute();
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

    static class JsonBuilder {
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

    static class ResponseChecker {
        HttpResponse response;
//        String content;

        static ResponseChecker assertFor(Response response) throws IOException {
            return new ResponseChecker(response);
        }

        ResponseChecker codeEqual(int expectedCode) {
            Assert.assertEquals("Wrong code.", expectedCode, response.getStatusLine().getStatusCode());
            return this;
        }

        ResponseChecker haveHeader(String headerName, int count) {
            Assert.assertNotNull(response.getHeaders(headerName));
            Assert.assertEquals(count, response.getHeaders(headerName).length);
            return this;
        }

        ResponseChecker(Response response) throws IOException {
            this.response = response.returnResponse();
//            this.content = response.returnContent().asString();
            response.returnResponse().getEntity().getContent()
        }
    }
}