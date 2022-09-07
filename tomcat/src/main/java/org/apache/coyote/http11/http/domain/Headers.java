package org.apache.coyote.http11.http.domain;

import java.io.BufferedReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Headers {

    private static final String BLANK = "";
    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_KEY = 0;
    private static final int HEADER_VALUE = 1;
    private static final String END_OF_LINE = " ";
    private static final String CRLF = "\r\n";

    private final Map<String, String> value;

    private Headers(final Map<String, String> value) {
        this.value = value;
    }

    public static Headers from(final BufferedReader bufferedReader) {
        return new Headers(bufferedReader.lines()
                .takeWhile(line -> !line.equals(BLANK))
                .map(line -> line.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(line -> line[HEADER_KEY], line -> line[HEADER_VALUE])));
    }

    public static Headers builder() {
        return new Headers(new LinkedHashMap<>());
    }

    public Headers setCookie(final UUID uuid) {
        value.put("Set-Cookie", "JSESSIONID=" + uuid.toString());
        return this;
    }

    public Headers contentType(final ContentType contentType) {
        value.put("Content-Type", contentType.getValue() + ";charset=utf-8");
        return this;
    }

    public Headers contentLength(final int length) {
        value.put("Content-Length", String.valueOf(length));
        return this;
    }

    public Headers location(final String location) {
        value.put("Location", location);
        return this;
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(value.get("Cookie"));
    }

    public String getHeaders() {
        return value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue() + END_OF_LINE)
                .collect(Collectors.joining(CRLF));
    }

    public Map<String, String> getValue() {
        return value;
    }
}
