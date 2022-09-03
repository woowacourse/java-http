package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final RequestUri requestUri;
    private final Map<String, String> headers;

    public HttpRequest(final RequestUri requestUri, final Map<String, String> headers) {
        this.requestUri = requestUri;
        this.headers = headers;
    }

    public static HttpRequest of(List<String> inputs) {
        String startLine = inputs.get(0);
        return new HttpRequest(RequestUri.of(startLine.split(" ")[1]), parseHeaders(inputs));
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

    public RequestUri getRequestUri() {
        return requestUri;
    }
}
