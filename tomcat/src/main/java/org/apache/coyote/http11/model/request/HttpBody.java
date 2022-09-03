package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public class HttpBody {
    private final Map<String, String> body;

    private HttpBody(final Map<String, String> body) {
        this.body = body;
    }

    public static HttpBody from(final String input) {
        Map<String, String> body = new HashMap<>();
        if(input.isBlank()) {
            return new HttpBody(body);
        }
        String[] splitBody = input.split("&");
        for (String s : splitBody) {
            String[] split = s.split("=");
            body.put(split[0], split[1]);
        }
        return new HttpBody(body);
    }

    public Map<String, String> getBody() {
        return body;
    }
}
