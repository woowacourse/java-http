package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody1 {

    private final String body;

    private RequestBody1(String body) {
        this.body = body;
    }

    public static RequestBody1 from(String body) {
        return new RequestBody1(body);
    }

    private Map<String, String> getParameters() {
        if (body.isEmpty()) {
            return Map.of();
        }
        final String[] dividedBody = body.split("&");
        return Arrays.stream(dividedBody)
                .map(query -> query.split("="))
                .collect(Collectors.toMap(key -> key[0].trim(), value -> value[1].trim()));
    }

    public String getBodyValue(String key) {
        final Map<String, String> parameters = getParameters();
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        return "";
    }
}
