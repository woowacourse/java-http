package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers;

    public static Headers from(List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        lines.stream()
                .map(each -> each.split(": "))
                .forEach(each -> headers.put(each[0], each[1]));

        return new Headers(headers);
    }

    private Headers(Map<String, String> headers) {
        this.headers = headers;
    }
}
