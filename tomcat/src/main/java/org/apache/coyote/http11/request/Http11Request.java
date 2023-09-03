package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11Request {

    private static final String LINE_SEPARATOR = "\r\n";
    final Method method;
    final String uri;

    final Map<String, String> headers;

    public Http11Request(final Method method, final String uri, final Map<String, String> headers) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
    }

    public static Http11Request from(final String request) {
        final List<String> lines = new ArrayList<>(List.of(request.split(LINE_SEPARATOR)));

        final String[] requestUri = lines.remove(0).split(" ");
        final String method = requestUri[0];
        final String uri = requestUri[1];

        final Map<String, String> headers = new HashMap<>();
        for (final String line : lines) {
            if ("".equals(line)) {
                break;
            }
            final String[] header = line.split(": ", 2);
            headers.put(header[0], header[1]);
        }

        return new Http11Request(
                Method.getMethod(method),
                uri,
                headers
        );
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }
}
