package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class RequestBody {

    public static final RequestBody EMPTY = new RequestBody(Map.of());
    private static final int KEY_VALUE_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int BODY_INDEX = 1;
    private static final String BODY_KEY_VALUE_SEPARATOR = "=";
    private static final String BODY_SEPARATOR = "&";

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody parse(String requestBody) {
        return Arrays.stream(requestBody.split(BODY_SEPARATOR))
                .map(query -> query.split(BODY_KEY_VALUE_SEPARATOR))
                .filter(body -> body.length == KEY_VALUE_SIZE)
                .collect(collectingAndThen(toMap(body -> body[KEY_INDEX], body -> body[BODY_INDEX]), RequestBody::new));
    }

    public String getBody(String key) {
        return body.getOrDefault(key, "");
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "body=" + body +
                '}';
    }
}
