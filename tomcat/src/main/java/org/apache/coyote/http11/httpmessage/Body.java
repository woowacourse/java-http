package org.apache.coyote.http11.httpmessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Body {

    private final Map<String, String> body;

    private Body(Map<String, String> body) {
        this.body = body;
    }

    public static Body from(List<String> lines) {
        Map<String, String> body = new HashMap<>();

        lines.stream()
                .map(each -> each.split("="))
                .forEach(each -> body.put(each[0], each[1]));

        return new Body(body);
    }

    public String get(String key) {
        return body.get(key);
    }
}
