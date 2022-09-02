package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.StaticResource;

public class ResponseHeaders {

    private final Map<String, String> value;

    public ResponseHeaders(final Map<String, String> value) {
        this.value = value;
    }

    public static ResponseHeaders withStaticResource(final StaticResource staticResource) {
        final var headers = new LinkedHashMap<String, String>();
        headers.put("Content-Type", staticResource.getContentType());
        headers.put("Content-Length", staticResource.getContentLength());
        return new ResponseHeaders(headers);
    }

    public static ResponseHeaders withLocation(final String location) {
        final var headers = new LinkedHashMap<String, String>();
        headers.put("Location", location);
        return new ResponseHeaders(headers);
    }

    public void add(final String key, final String value) {
        this.value.put(key, value);
    }

    @Override
    public String toString() {
        return value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
