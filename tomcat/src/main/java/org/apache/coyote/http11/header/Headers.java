package org.apache.coyote.http11.header;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Headers {
    private final Map<HeaderType, String> headers;

    public Headers(final Map<HeaderType, String> headers) {
        this.headers = headers;
    }

    public static Headers parse(final List<String> headerLines) {
        final var headers = new EnumMap<HeaderType, String>(HeaderType.class);
        headerLines.stream()
                .map(line -> line.split(": "))
                .forEach(header -> headers.put(HeaderType.from(header[0]), header[1].trim()));
        return new Headers(headers);
    }

    public ContentType getContentType() {
        return ContentType.of(headers.getOrDefault(HeaderType.CONTENT_TYPE, ""));
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(HeaderType.CONTENT_LENGTH, "0"));
    }

    public Cookies getCookies() {
        if (headers.containsKey(HeaderType.COOKIE)) {
            return Cookies.parse(headers.get(HeaderType.COOKIE));
        }
        return Cookies.empty();
    }
}
