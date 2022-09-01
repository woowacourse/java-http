package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Map<String, String> value;

    public Headers(final Map<String, String> value) {
        this.value = value;
    }

    public static Headers withStaticResource(final StaticResource staticResource) {
        final var headers = new LinkedHashMap<String, String>();
        headers.put("Content-Type", staticResource.getContentType());
        headers.put("Content-Length", staticResource.getContentLength());
        return new Headers(headers);
    }

    @Override
    public String toString() {
        return value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining(NEW_LINE));
    }
}
