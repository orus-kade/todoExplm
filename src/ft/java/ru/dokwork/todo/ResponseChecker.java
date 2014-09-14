package ru.dokwork.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for check http response.
 */
public class ResponseChecker {

    private static final String EMPTY_CONTENT = "Content is empty.";
    private static final String WRONG_CODE = "Wrong code. Expected %1d but was %2d.";
    private static final String HEADER_NOT_FOUND = "Header '%1s' is not found.";
    private static final String WRONG_HEADERS_COUNT = "Wrong headers count for '%1s'. Expected %2d but was %3d.";
    private static final String WRONG_CONTENT = "Wrong content.\nExpected: \n'%1s'\nbut was\n'%2s'.";
    private static final String WRONG_ARRAY_SIZE = "Wrong array size. Expected %1d but was %2d.";
    private static final String ELEMENT_NOT_FOUND = "Element %1s is not found in actual array." +
            "\nExpected array: \n%2s \nbut was: \n%3s.";

    HttpResponse response;
    String content;
    JsonParser parser;

    /**
     * Create assertions for http response.
     *
     * @param response
     * @return {@link ResponseChecker}
     * @throws IOException
     */
    public static ResponseChecker assertThat(HttpResponse response) throws IOException {
        return new ResponseChecker(response);
    }

    /**
     * Assert that response have code number equal {@code expectedCode}.
     *
     * @param expectedCode expected code number in http response.
     * @return {@link ResponseChecker}
     */
    public ResponseChecker haveCode(int expectedCode) {
        int actualCode = response.getStatusLine().getStatusCode();
        if (actualCode != expectedCode) {
            throw new WronAssertionException(String.format(WRONG_CODE, expectedCode, actualCode));
        }
        return this;
    }

    /**
     * Assert that response have headers.
     *
     * @param headerName    name of header.
     * @param expectedCount expected headers count.
     * @return {@link ResponseChecker}
     */
    public ResponseChecker haveHeaders(String headerName, int expectedCount) {
        if (response.getHeaders(headerName) == null) {
            throw new WronAssertionException(String.format(HEADER_NOT_FOUND, headerName));
        }
        int actualCount = response.getHeaders(headerName).length;
        if (expectedCount != actualCount) {
            throw new WronAssertionException(
                    String.format(WRONG_HEADERS_COUNT, headerName, expectedCount, actualCount));
        }
        return this;
    }

    /**
     * Assert that response have content equals to json object.
     *
     * @param expectedContent expected json object in response content.
     * @return {@link ResponseChecker}
     */
    public ResponseChecker haveContent(JsonObject expectedContent) {
        if (content.isEmpty()) {
            throw new WronAssertionException(EMPTY_CONTENT);
        }
        JsonObject actualContent = parser.parse(content).getAsJsonObject();
        if (!expectedContent.equals(actualContent)) {
            throw new WronAssertionException(String.format(WRONG_CONTENT, expectedContent, actualContent));
        }
        return this;
    }

    /**
     * Assert that response have content equals to json array.
     *
     * @param expectedContent expected json array in response content.
     * @return {@link ResponseChecker}
     */
    public ResponseChecker haveContent(JsonArray expectedContent) {
        if (content.isEmpty()) {
            throw new WronAssertionException(EMPTY_CONTENT);
        }
        JsonArray actualContent = parser.parse(content).getAsJsonArray();
        if (expectedContent.size() != actualContent.size()) {
            throw new WronAssertionException(String.format(WRONG_ARRAY_SIZE,
                    expectedContent.size(), actualContent.size()));
        }
        List<JsonElement> actualElements = new LinkedList<>();
        for (JsonElement actualElement : actualContent) {
            actualElements.add(actualElement);
        }
        expected:
        for (JsonElement expectedElement : expectedContent) {
            for (JsonElement actualElement : actualElements) {
                if (expectedElement.equals(actualElement)) {
                    actualElements.remove(actualElement);
                    continue expected;
                }
            }
            throw new WronAssertionException(
                    String.format(ELEMENT_NOT_FOUND, expectedElement, expectedContent, actualContent));
        }
        return this;
    }

    private ResponseChecker(HttpResponse response) throws IOException {
        this.response = response;
        this.content = readResponseToString(response);
        this.parser = new JsonParser();
    }

    private static String readResponseToString(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        java.util.Scanner s = new java.util.Scanner(entity.getContent()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
