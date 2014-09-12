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
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

public class TaskServiceFT {

    private static final String URL = "http://localhost:8080/todo/tasks/";

    static final JsonObject jtask1;

    static final JsonObject jtask2;

    static final JsonParser parser;

    static {
        jtask1 = new JsonObject();
        jtask1.addProperty("name", "First task");
        jtask1.addProperty("description", "First task for test.");

        jtask2 = new JsonObject();
        jtask2.addProperty("name", "Second task");
        jtask2.addProperty("description", "Second task for test.");

        parser = new JsonParser();
    }

    @After
    public void tearDown() throws Exception {
        HttpResponse httpResponse = getAll();
        JsonArray array = parser.parse(ResponseChecker.readResponseToSring(httpResponse)).getAsJsonArray();
        for (JsonElement jsonElement : array) {
            JsonObject jtask = jsonElement.getAsJsonObject();
            delete(UUID.fromString(jtask.get("uuid").getAsString()));
        }
        Assert.assertTrue(ResponseChecker.readResponseToSring(getAll()).isEmpty());
    }

    @Test
    public void functionalTest() throws Exception {
        // Add first task
        HttpResponse response = addNewTask(jtask1);
        // Check answer
        ResponseChecker.assertFor(response).codeEqual(201).haveHeaders("location", 1);
        String uuid1 = response.getFirstHeader("location").getValue().substring(URL.length());

        // Get first task
        response = getTask(UUID.fromString(uuid1));
        String answer = ResponseChecker.readResponseToSring(response);
        // Check answer
        JsonObject expectedObject = createJsonTask(uuid1, jtask1);
        ResponseChecker.assertFor(response).codeEqual(200).contentIs(expectedObject);

        // Add second task
        response = addNewTask(jtask2);
        // Check answer
        ResponseChecker.assertFor(response).codeEqual(201).haveHeaders("location", 1);
        String uuid2 = response.getFirstHeader("location").getValue().substring(URL.length());

        // Get all tasks
        response = getAll();
        // Check answer
        JsonArray array = new JsonArray();
        array.add(createJsonTask(uuid1, jtask1));
        array.add(createJsonTask(uuid2, jtask2));
        ResponseChecker.assertFor(response).codeEqual(200).contentIs(array);
    }

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

    private JsonObject createJsonTask(String uuid, JsonObject pattern) {
        JsonObject expectedObject = new JsonObject();
        expectedObject.add("uuid", parser.parse(uuid));
        expectedObject.add("name", pattern.get("name"));
        expectedObject.add("description", pattern.get("description"));
        expectedObject.add("completed", parser.parse("false"));
        return expectedObject;
    }

    static class ResponseChecker {

        public static String readResponseToSring(HttpResponse response) throws IOException {
            java.util.Scanner s = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        HttpResponse response;
        String content;
        JsonParser parser;

        static ResponseChecker assertFor(HttpResponse response) throws IOException {
            return new ResponseChecker(response);
        }

        ResponseChecker codeEqual(int expectedCode) {
            Assert.assertEquals("Wrong code.", expectedCode, response.getStatusLine().getStatusCode());
            return this;
        }

        ResponseChecker haveHeaders(String headerName, int count) {
            Assert.assertNotNull(response.getHeaders(headerName));
            Assert.assertEquals(count, response.getHeaders(headerName).length);
            return this;
        }

        ResponseChecker contentIs(JsonObject expectedContent) {
            Assert.assertFalse("Content is empty.", content.isEmpty());
            JsonObject jcontent = parser.parse(content).getAsJsonObject();
            Assert.assertEquals("Wrong content.", expectedContent, jcontent);
            return this;
        }

        ResponseChecker contentIs(JsonArray expectedContent) {
            Assert.assertFalse("Content is empty.", content.isEmpty());
            JsonArray jcontent = parser.parse(content).getAsJsonArray();
            Assert.assertEquals("Wrong array size.", expectedContent.size(), jcontent.size());
            HashSet actual = new HashSet();
            for (JsonElement jsonElement : jcontent) {
                actual.add(jsonElement);
            }
            HashSet expected = new HashSet();
            for (JsonElement jsonElement : expectedContent) {
                expected.add(jsonElement);
            }
            Assert.assertTrue("Arrays not equals.", expected.contains(actual));
            return this;
        }

        ResponseChecker(HttpResponse response) throws IOException {
            this.response = response;
            this.content = readResponseToSring(response);
            this.parser = new JsonParser();
        }
    }
}