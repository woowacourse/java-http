package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    public static final String BODY_SEPERATOR = "=";
    private static final String SEPERATOR = "&";
    private final Map<String, String> body;

    private HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static HttpRequestBody from(String requestBody) {
        Map<String, String> body = new HashMap<>();

        String[] keyValuePairs = requestBody.split("&");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(BODY_SEPERATOR);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                body.put(key, value);
            }
        }
        return new HttpRequestBody(body);
    }

    public String getValue(String name) {
        return body.get(name);
    }
}
