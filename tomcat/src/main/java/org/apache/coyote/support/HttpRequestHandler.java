package org.apache.coyote.support;

import java.io.IOException;
import java.util.List;

public class HttpRequestHandler {

    private final Method method;
    private final String uri;

    private HttpRequestHandler(Method method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public static HttpRequestHandler of(List<String> request) {
        final var startLine = request.get(0).split(" ");
        final var method = Method.valueOf(startLine[0]);
        var uri = startLine[1];
        if (Method.GET.equals(method) && uri.contains("?")) {
            final var uriAndParams = uri.split("\\?");
            uri = uriAndParams[0];
        }
        return new HttpRequestHandler(method, uri);
    }

    public String handle() throws IOException {
        if (method.equals(Method.GET)) {
            return get();
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    private String get() throws IOException {
        ResourceResponse resourceResponse = new ResourceResponse(uri);
        String responseBody = resourceResponse.toContent();
        String contentType = resourceResponse.toContentType();
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                String.format("Content-Type: %s;charset=utf-8 ", contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
