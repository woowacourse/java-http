package org.apache.coyote.http11;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeaders {

    private final Map<String, String> httpHeaders;

    public HttpHeaders(final Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public static HttpHeaders from(final List<String> httpHeaders) {
        final Map<String, String> parsedHttpHeaders = new HashMap<>();

        for (final String httpHeader : httpHeaders) {
            final String[] keyValue = httpHeader.split(": ");
            parsedHttpHeaders.put(keyValue[0], keyValue[1]);
        }

        return new HttpHeaders(parsedHttpHeaders);
    }

    public ContentLength getContentLength() {
        final Entry<String, String> contentLength = httpHeaders.entrySet().stream()
                .filter(it -> it.getKey().equalsIgnoreCase("content-length"))
                .findFirst()
                .orElseGet(() -> new SimpleEntry<>("Content-length", "0"));
        return ContentLength.from(contentLength.getValue());
    }
}
