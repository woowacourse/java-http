package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {
    private static final String INPUT_SPLIT_REGEX = "&";
    private static final String BODY_SPLIT_REGEX = "=";

    private final Map<String, String> values;

    private RequestBody(Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody getEmptyBody() {
        return new RequestBody(Map.of());
    }

    public static RequestBody from(String input) {
        Map<String, String> values = Arrays.stream(input.split(INPUT_SPLIT_REGEX))
            .map(body -> body.split(BODY_SPLIT_REGEX))
            .collect(Collectors.toMap(query -> query[0], query -> query[1]));
        return new RequestBody(values);
    }

    public String get(String input) {
        return values.get(input);
    }
}
