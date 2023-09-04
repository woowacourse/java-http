package org.apache.coyote.http11;

import java.util.Map;

import static org.apache.coyote.http11.HttpStatusCode.NOT_FOUND;

public class HttpResponse {

    private HttpStatusCode statusCode;
    private final HttpHeaders headers;
//    private final Map<String, String> header;
    private String body;

    private HttpResponse(final HttpStatusCode statusCode, final HttpHeaders headers, final String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse init() {
        return new HttpResponse(NOT_FOUND, new HttpHeaders(), "");
    }

    public void addHeader(final String key, final String value) {
        headers.setHeaderValue(key, value);
    }

    public void addHeader(final HttpHeaderType key, final String value) {
        headers.setHeaderValue(key, value);
    }

    public void setStatusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public String stringify() {
        return String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode.stringify() + " ",
                stringifyHeaders(),
                "",
                body
        );
    }

    private String stringifyHeaders() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.getHeaders().entrySet()) {
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
