package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(String body) {
        return new RequestBody(getRequestBody(body));
    }

    private static Map<String, String> getRequestBody(String body) {
        if (body.isEmpty()) {
            return Map.of();
        }
        final String[] dividedBody = body.split("&");
        return Arrays.stream(dividedBody)
                .map(query -> query.split("="))
                .collect(Collectors.toMap(key -> key[0].trim(), value -> value[1].trim()));
    }

    public String getBodyValue(String key) {
        if (body.containsKey(key)) {
            return body.get(key);
        }
        return "";
    }
}
