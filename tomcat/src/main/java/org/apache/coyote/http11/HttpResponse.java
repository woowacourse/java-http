package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String BLANK = " ";
    private static final String CRLF = "\r\n";

    private final HttpStatus status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final String body;

    private HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public static HttpResponse ok(String contentType, String body) {
        HttpResponse response = new HttpResponse(HttpStatus.OK, body);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        return response;
    }

    public static HttpResponse notFound(String contentType, String body) {
        HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND, body);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        return response;
    }

    public static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse(HttpStatus.FOUND, "");
        response.addHeader("Location", location);
        return response;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String toHttpMessage() {
        StringBuilder message = new StringBuilder();
        message.append(HTTP_VERSION).append(BLANK)
                .append(status.getCode()).append(BLANK)
                .append(status.getReasonPhrase()).append(BLANK)
                .append(CRLF);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            message.append(header.getKey()).append(": ")
                    .append(header.getValue()).append(BLANK)
                    .append(CRLF);
        }

        message.append(CRLF).append(body);
        return message.toString();
    }
}
