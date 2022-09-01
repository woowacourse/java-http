package org.apache.coyote.support;

import java.io.IOException;
import java.util.List;
import org.apache.exception.NotFoundException;

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
        try {
            return new ResourceResponse(uri).toHttpResponseMessage();
        } catch (NotFoundException e) {
            return ResourceResponse.ofNotFound().toHttpResponseMessage();
        }
    }
}
