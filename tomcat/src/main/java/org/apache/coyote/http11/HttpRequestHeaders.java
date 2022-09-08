package org.apache.coyote.http11;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.catalina.Cookies;

public class HttpRequestHeaders {

    private final Map<String, String> httpRequestHeaders;

    public HttpRequestHeaders(final Map<String, String> httpHeaders) {
        this.httpRequestHeaders = httpHeaders;
    }

    public static HttpRequestHeaders from(final List<String> httpHeaders) {
        final Map<String, String> parsedHttpHeaders = new HashMap<>();

        for (final String httpHeader : httpHeaders) {
            final String[] keyValue = httpHeader.split(": ");
            parsedHttpHeaders.put(keyValue[0], keyValue[1]);
        }

        return new HttpRequestHeaders(parsedHttpHeaders);
    }

    public ContentLength getContentLength() {
        final Entry<String, String> contentLength = httpRequestHeaders.entrySet().stream()
                .filter(it -> it.getKey().equalsIgnoreCase("content-length"))
                .findFirst()
                .orElseGet(() -> new SimpleEntry<>("Content-length", "0"));
        return ContentLength.from(contentLength.getValue());
    }

    public Cookies getCookies() {
        final String cookies = httpRequestHeaders.entrySet().stream()
                .filter(it -> it.getKey().equalsIgnoreCase("Cookie"))
                .map(Entry::getValue)
                .findFirst()
                .orElseGet(() -> "EMPTY");
        return Cookies.from(cookies);
    }
}
