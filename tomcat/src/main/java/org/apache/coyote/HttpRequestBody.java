package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> body = new HashMap<>();

    public HttpRequestBody(String bodyLine) {
        for (String parameter : bodyLine.split("&")) {
            int index = parameter.indexOf("=");
            if (index == -1) {
                break;
            }
            String key = parameter.substring(0, index).trim();
            String value = parameter.substring(index + 1).trim();
            body.put(key, value);
        }
    }

    public Map<String, String> getBody() {
        return body;
    }
}
