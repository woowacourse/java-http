package org.apache.coyote.http11;

import java.util.HashMap;

public class HttpBody {

    private final HashMap<String, String> body;

    public HttpBody(String requestBody) {
        HashMap<String, String> map = new HashMap<>();

        String[] requestBodies = requestBody.split("&");

        for (String s : requestBodies) {
            if (s.isEmpty()) {
                continue;
            }
            String[] keyValues = s.split("=");
            map.put(keyValues[0], keyValues[1]);
        }

        this.body = map;
    }

    public HashMap<String, String> getBody() {
        return body;
    }
}
