package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> value;

    public HttpResponseHeader(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpResponseHeader fromContentType(final ContentType contentType) {
        final Map<String, String> value = new HashMap<>();
        value.put(CONTENT_TYPE, contentType.getValue());
        return new HttpResponseHeader(value);
    }

    public void addLocation(final String location) {
        value.put("Location", location);
    }

    public void setCookie(final String value) {
        this.value.put(SET_COOKIE, value);
    }

    public String getAll() {
        return value.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
