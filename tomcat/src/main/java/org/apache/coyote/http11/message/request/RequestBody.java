package org.apache.coyote.http11.message.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(String line) {
        Map<String, String> body = new HashMap<>();

        Arrays.stream(line.split("&"))
                .map(each -> each.split("="))
                .forEach(each -> body.put(each[0], each[1]));

        return new RequestBody(body);
    }

    public static RequestBody ofEmpty() {
        return new RequestBody(Collections.emptyMap());
    }

    public String get(String key) {
        return body.get(key);
    }
}
