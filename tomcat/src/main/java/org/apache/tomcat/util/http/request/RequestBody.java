package org.apache.tomcat.util.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestBody {

    private final Map<String, String> body;

    public RequestBody(Optional<String> content) {
        if (content.isPresent() && !content.get().isEmpty()) {
            this.body = parseBody(content.get());
            return;
        }
        this.body = new HashMap<>();
    }

    public boolean containKey(String key) {
        return body.containsKey(key);
    }

    public String getValue(String key) {
        return body.get(key);
    }

    private Map<String, String> parseBody(String content) {
        Map<String, String> bodyRead = new HashMap<>();

        List<String> bodyParts = List.of(content.split("&"));
        for (String bodyPart : bodyParts) {
            List<String> keyValue = List.of(bodyPart.split("="));
            String key = keyValue.getFirst().trim();
            String value = keyValue.getLast().trim();
            bodyRead.put(key, value);
        }
        return bodyRead;
    }
}
