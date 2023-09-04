package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class RequestBody {

    public static final RequestBody EMPTY = new RequestBody(Map.of());

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody parse(String requestBody) {
        return Arrays.stream(requestBody.split("&"))
                .map(query -> query.split("="))
                .collect(collectingAndThen(toMap(query -> query[0], query -> query[1]), RequestBody::new));
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
