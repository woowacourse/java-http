package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        Map<String, String> requestBody = new HashMap<>();
        String[] split = body.split("&");
        for (String line : split) {
            String[] component = line.split("=");
            requestBody.put(component[0], component[1]);
        }
        return requestBody;
    }
}
