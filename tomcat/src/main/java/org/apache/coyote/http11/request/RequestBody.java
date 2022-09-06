package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getParsedBody() {
        String[] keyValues = body.split("&");

        Map<String, String> queryMap = new HashMap<>();
        for (String keyValue : keyValues) {
            String key = keyValue.split("=")[0];
            String value = keyValue.split("=")[1];
            queryMap.put(key, value);
        }
        return queryMap;
    }
}
