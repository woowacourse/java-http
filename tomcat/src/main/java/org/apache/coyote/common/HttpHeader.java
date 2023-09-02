package org.apache.coyote.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpHeader {

    ACCEPT("Accept"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    CONNECTION("Connection");

    private static final Map<String, HttpHeader> mapping = new HashMap<>(collectAllHeaders());

    private static Map<String, HttpHeader> collectAllHeaders() {
        return Arrays.stream(HttpHeader.values())
                .collect(Collectors.toMap(HttpHeader::source, Function.identity()));
    }

    private final String source;

    HttpHeader(final String source) {
        this.source = source;
    }

    public static HttpHeader from(final String name) {
        return mapping.get(name);
    }

    public String source() {
        return source;
    }
}
