package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class HttpResponseBody {

    private Map<String, String> body = new HashMap<>();

    public HttpResponseBody(Map<String, String> body) {
        this.body = new HashMap<>(body);
    }

    private HttpResponseBody() {
    }

    public static HttpResponseBody from(String body) {
        if (body.isEmpty()) {
            return HttpResponseBody.none();
        }
        return Arrays.stream(body.split("&"))
                .map(field -> field.split("="))
                .collect(collectingAndThen(
                        toMap(field -> field[0], field -> field[1]),
                        HttpResponseBody::new
                ));
    }

    private static HttpResponseBody none() {
        return new HttpResponseBody();
    }

    public String find(String key) {
        return body.get(key);
    }
}
