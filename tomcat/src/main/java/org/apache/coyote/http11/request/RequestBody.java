package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    public static final RequestBody EMPTY = new RequestBody(Map.of());

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody parse(String requestBody) {
        Map<String, String> body = Arrays.stream(requestBody.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));

        return new RequestBody(body);
    }

    public String getBody(String key) {
        if (body.containsKey(key)) {
            return body.get(key);
        }
        return "";
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "body=" + body +
                '}';
    }
}
