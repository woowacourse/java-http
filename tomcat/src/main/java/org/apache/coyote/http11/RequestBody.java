package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, String> parseRequestBody() {
        Map<String, String> bodyParams = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                bodyParams.put(keyValue[0], keyValue[1]);
            }
        }
        return bodyParams;
    }
}
