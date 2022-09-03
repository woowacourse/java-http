package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.apache.coyote.StatusLine;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(final HttpVersion httpVersion, final StatusCode statusCode, final ContentType contentType,
                        final String body) {
        this.statusLine = new StatusLine(httpVersion, statusCode);
        this.headers = generateHeaders(contentType, body);
        this.body = body;
    }

    private static Headers generateHeaders(final ContentType contentType, final String body) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType.getValue());
        headers.put("Content-Length", getContentLength(body));
        return new Headers(headers);
    }

    private static String getContentLength(final String body) {
        int contentLength = body.getBytes().length;
        return String.valueOf(contentLength);
    }

    public String parseResponse() {
        StringBuilder headers = new StringBuilder();
        LinkedHashMap<String, String> values = this.headers.getValues();
        for (Entry<String, String> header : values.entrySet()) {
            headers.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(" \r\n");
        }

        return statusLine.getStatusLineMessage() + " \r\n" +
                headers +
                "\r\n" +
                body;
    }
}
