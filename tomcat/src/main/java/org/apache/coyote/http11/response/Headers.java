package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> value;

    public Headers(Map<String, String> value) {
        this.value = value;
    }

    public static Headers withStaticResource(final StaticResource staticResource) {
        final var headers = new HashMap<String, String>();
        headers.put("Content-Type", staticResource.getContentType());
        headers.put("Content-Length", staticResource.getContentLength());
        return new Headers(headers);
    }

    @Override
    public String toString() {
        return value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining());
    }
}
