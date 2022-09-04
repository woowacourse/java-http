package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.StaticResource;

public class ResponseHeader {

    private final Map<String, String> values;

    public ResponseHeader(final Map<String, String> values) {
        this.values = values;
    }

    public static ResponseHeader withStaticResource(final StaticResource staticResource) {
        final var headers = new LinkedHashMap<String, String>();
        headers.put("Content-Type", staticResource.getContentType());
        headers.put("Content-Length", staticResource.getContentLength());
        return new ResponseHeader(headers);
    }

    public static ResponseHeader withLocation(final String location) {
        final var headers = new LinkedHashMap<String, String>();
        headers.put("Location", location);
        return new ResponseHeader(headers);
    }

    public void add(final String key, final String value) {
        this.values.put(key, value);
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
