package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    public static final RequestBody EMPTY = new RequestBody(Collections.emptyMap());

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(final String requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            return EMPTY;
        }

        String[] keyValues = requestBody.split("&");

        Map<String, String> body = Arrays.stream(keyValues)
                .map(keyValue -> keyValue.split("=", 2))
                .filter(split -> split.length == 2)
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));

        return new RequestBody(body);
    }

    public String get(final String key) {
        return body.get(key);
    }

}
