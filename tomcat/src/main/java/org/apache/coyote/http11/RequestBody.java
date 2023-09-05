package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class RequestBody {

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody empty() {
        return new RequestBody(new HashMap<>());
    }

    public static RequestBody parse(final String bodyLine) {
        if (bodyLine == null) {
            return new RequestBody(new HashMap<>());
        }
        return Arrays.stream(bodyLine.split("&"))
                .map(field -> field.split("="))
                .collect(collectingAndThen(
                        toMap(field -> field[0], field -> field[1]),
                        RequestBody::new
                ));
    }

    public Map<String, String> getBody() {
        return body;
    }
}
