package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestBody {

    private final Map<String, String> requestBody;

    private HttpRequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public static HttpRequestBody from(String requestBody) {
        return new HttpRequestBody(parseRequestBody(requestBody));
    }

    private static Map<String, String> parseRequestBody(String requestBody) {
        Map<String, String> parseResultOfRequestBody = new ConcurrentHashMap<>();

        for (String component : requestBody.trim().split("&")) {
            putParseResultOfComponent(parseResultOfRequestBody, component);
        }

        return parseResultOfRequestBody;
    }

    private static void putParseResultOfComponent(Map<String, String> parseResultOfRequestBody, String component) {
        if (component.length() < 2) {
            return;
        }

        String[] keyAndValue = component.split("=");
        parseResultOfRequestBody.put(keyAndValue[0].trim(), keyAndValue[1].trim());
    }

    public String get(String key) {
        return requestBody.get(key);
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

}
