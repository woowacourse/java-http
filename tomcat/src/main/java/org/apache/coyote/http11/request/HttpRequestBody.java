package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class HttpRequestBody {

    private Map<String, String> body = new HashMap<>();

    private HttpRequestBody() {
    }

    private HttpRequestBody(Map<String, String> body) {
        this.body = new HashMap<>(body);
    }

    public static HttpRequestBody none() {
        return new HttpRequestBody();
    }

    public static HttpRequestBody from(String body) {
        if (body.isEmpty()) {
            return HttpRequestBody.none();
        }
        return Arrays.stream(body.split("&"))
                .map(field -> field.split("="))
                .collect(collectingAndThen(
                        toMap(field -> field[0], field -> field[1]),
                        HttpRequestBody::new
                ));
    }

    public String find(String key) {
        return body.get(key);
    }
}
