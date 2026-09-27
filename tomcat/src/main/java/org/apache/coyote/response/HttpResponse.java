package org.apache.coyote.response;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {
    public static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    public static final String CRLF = "\r\n";

    private final StatusLine statusLine;
    private final Map<String, String> headers;
    private final String body;

    public static HttpResponse build(Map<String, String> headers, String body){
        StatusCode statusCode = StatusCode.OK;
        if (headers.containsKey(LOCATION)) {
            statusCode = StatusCode.FOUND;
        }

        return new HttpResponse(StatusLine.build(statusCode), headers, body);
    }

    private HttpResponse(StatusLine statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String getResponse() {
        StringBuilder response = new StringBuilder();
        response.append(statusLine);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(CRLF);
        }

        response.append(CONTENT_LENGTH).append(": ")
                .append(body.getBytes(StandardCharsets.UTF_8).length)
                .append(" ")
                .append(CRLF);
        response.append(CRLF);
        response.append(body);

        return response.toString();
    }
}
