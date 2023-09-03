package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {

    private final String statusCode;
    private final Map<String, String> header;
    private final String body;

    private HttpResponse(final String statusCode, final Map<String, String> header, final String body) {
        this.statusCode = statusCode;
        this.header = header;
        this.body = body;
    }

    public static HttpResponse of(final String statusCode, final Map<String, String> header, final String body) {
        return new HttpResponse(statusCode, header, body);
    }

    public void addHeader(final String key, final String value) {
        header.put(key, value);
    }

    public String stringify() {
        return String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode + " ",
                stringifyHeaders(),
                "",
                body
        );
    }

    private String stringifyHeaders() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" \r\n");
        }
        stringBuilder.append("Content-Length: ")
                .append(body.getBytes().length)
                .append(" ");

        return stringBuilder.toString();
    }
}
