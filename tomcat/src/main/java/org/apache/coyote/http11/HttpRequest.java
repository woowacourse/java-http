package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final RequestUri uri;
    private final Map<String, String> headers;

    private HttpRequest(final RequestUri uri, final Map<String, String> headers) {
        this.uri = uri;
        this.headers = headers;
    }

    public static HttpRequest of(List<String> inputs) {
        final String uri = inputs.get(0).split(" ")[1];
        return new HttpRequest(RequestUri.of(uri), parseHeaders(inputs));
    }

    private static Map<String, String> parseHeaders(final List<String> inputs) {
        Map<String, String> headers = new HashMap<>();
        for (String header : inputs.subList(1, inputs.size())) {
            String[] splitHeader = header.split(": ", 2);
            headers.put(splitHeader[0], splitHeader[1]);
        }
        return headers;
    }

    public String findContentType() {
        return headers.getOrDefault("Accept", DEFAULT_CONTENT_TYPE).split(",")[0];
    }

    public RequestUri getRequestUri() {
        return uri;
    }
}
