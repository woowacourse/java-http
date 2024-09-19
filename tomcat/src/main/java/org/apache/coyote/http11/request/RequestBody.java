package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final int KEY = 0;
    private static final int VALUE = 1;
    private final Map<String, String> body;

    public RequestBody() {
        this.body = new HashMap<>();
    }

    public RequestBody(String body) {
        this.body = convertBody(body);
    }

    public Map<String, String> convertBody(String body) {
        Map<String, String> requestBody = new HashMap<>();
        String[] split = body.split("&");
        for (String line : split) {
            String[] component = line.split("=");
            requestBody.put(component[KEY], component[VALUE]);
        }
        return requestBody;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
