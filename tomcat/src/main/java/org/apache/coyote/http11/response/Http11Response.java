package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Response {
    private static final String LINE_SEPARATOR = "\r\n";

    final Status status;
    final Map<String, String> headers;
    final String body;

    public Http11Response(final Status status, final String body) {
        this.status = status;
        this.headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8 ");
        headers.put("Content-Length", body.getBytes().length + " ");

        this.body = body;
    }

    public void addHeader(final String headerName, final String value) {
        headers.put(headerName, value);
    }

    public String getResponse() {
        final String formattedStatus = formatStatus(status);
        final String formattedHeaders = headers.keySet().stream()
                .map(this::formatHeader)
                .collect(Collectors.joining(LINE_SEPARATOR));

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 " + formattedStatus,
                formattedHeaders,
                "",
                body);
    }

    private String formatStatus(final Status s) {
        return s.getCode() + " " + s.getName() + " ";
    }

    private String formatHeader(final String h) {
        return h + ": " + headers.get(h);
    }
}
