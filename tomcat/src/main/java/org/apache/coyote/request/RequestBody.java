package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> body;

    public RequestBody() {
        this.body = new HashMap<>();
    }

    public RequestBody(String bodyLine) {
        this.body = mapBody(bodyLine);
    }

    private Map<String, String> mapBody(String bodyLine) {
        Map<String, String> rawBody = new HashMap<>();

        String[] bodyElements = bodyLine.split("&");
        for (int i = 0; i < bodyElements.length; i++) {
            String[] info = bodyElements[i].split("=");
            rawBody.put(info[0], info[1]);
        }
        return rawBody;
    }

    public String get(String key) {
        return body.get(key);
    }
}
