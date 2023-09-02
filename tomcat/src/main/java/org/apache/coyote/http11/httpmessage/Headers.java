package org.apache.coyote.http11.httpmessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Headers {

    private final Map<String, String> headers;

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers from(List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        lines.stream()
                .map(each -> each.split(": "))
                .forEach(each -> headers.put(each[0], each[1]));

        return new Headers(headers);
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        headers.forEach((key, value) -> stringJoiner.add(key + ": " + value + " "));
        return stringJoiner.toString();
    }
}
