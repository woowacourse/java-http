package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class HttpResponse {

    public static final HttpResponse DEFAULT_HTTP_RESPONSE;

    static {
        String statusLine = "HTTP/1.1 200 OK";
        String body = "Hello world!";

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getValue());
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        DEFAULT_HTTP_RESPONSE = new HttpResponse(statusLine, headers, body);
    }

    private final String statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(final String statusLine, final LinkedHashMap<String, String> headers, final String body) {
        this.statusLine = statusLine;
        this.headers = new Headers(headers);
        this.body = body;
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

        return statusLine + " \r\n" +
                headers +
                "\r\n" +
                body;
    }
}
