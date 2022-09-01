package org.apache.coyote.support;

import java.util.List;

public class HttpRequestMessage {

    private final Method method;
    private final String uri;

    private HttpRequestMessage(Method method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public static HttpRequestMessage of(List<String> request) {
        final var startLine = request.get(0).split(" ");
        final var method = Method.valueOf(startLine[0]);
        var uri = startLine[1];
        if (Method.GET.check(method) && uri.contains("?")) {
            final var uriAndParams = uri.split("\\?");
            uri = uriAndParams[0];
        }
        return new HttpRequestMessage(method, uri);
    }

    public boolean isGetMethod() {
        return method == Method.GET;
    }

    public String getUri() {
        return uri;
    }
}
