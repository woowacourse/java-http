package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final String startLine;
    private final Map<String, String> headers;

    public HttpRequest(final String startLine, final Map<String, String> headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public static HttpRequest of(List<String> inputs) {
        return new HttpRequest(inputs.get(0), parseHeaders(inputs));
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
        String accept = headers.getOrDefault("Accept", DEFAULT_CONTENT_TYPE);
        return accept.split(",")[0];
    }

    public RequestUri extractRequestUri() {
        String uri = startLine.split(" ")[1];
        return RequestUri.of(uri);
    }
}
