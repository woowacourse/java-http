package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Response {

    private static final String LINE_SEPARATOR = "\r\n";
    final Map<String, String> headers;
    final String body;

    public Http11Response(final Map<String, String> headers, final String body) {
        this.headers = headers;
        this.body = body;
    }

    public static Http11Response of(final String accept, final String body) {
        final Map<String, String> headers = new LinkedHashMap<>();

        headers.put("Content-Type", "text/html;charset=utf-8 ");
        if (accept != null && accept.contains("css")) {
            headers.replace("Content-Type", "text/css;charset=utf-8 ");
        }

        headers.put("Content-Length", body.getBytes().length + " ");

        return new Http11Response(headers, body);
    }


    public String getResponse() {
        final String header = headers.keySet().stream()
                .map(this::formatHeader)
                .collect(Collectors.joining(LINE_SEPARATOR));

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 200 OK ",
                header,
                "",
                body);
    }

    private String formatHeader(final String h) {
        return h + ": " + headers.get(h);
    }
}
