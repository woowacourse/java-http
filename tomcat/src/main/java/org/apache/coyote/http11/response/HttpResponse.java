package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.stream.Collectors;

public class HttpResponse {

    public static final String EMPTY_LINE = "";
    public static final String NEW_LINE = "\r\n";
    private HttpResponseStartLine httpResponseStartLine;
    private HttpResponseHeaders headers;
    private String responseBody;

    public HttpResponse() {
        this.headers = HttpResponseHeaders.empty();
    }

    public void setHttpResponseStartLine(StatusCode statusCode) {
        httpResponseStartLine = new HttpResponseStartLine("HTTP/1.1", statusCode);
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void setResponseBody(final byte[] responseBody) {
        headers.add("Content-Length", String.valueOf(responseBody.length));
        this.responseBody = new String(responseBody);
    }

    public byte[] generateResponse() throws IOException {
        return String.join(NEW_LINE,
                generateStartLine(),
                generateHeaders(),
                EMPTY_LINE,
                responseBody).getBytes();
    }

    private String generateStartLine() {
        return String.format("%s %s %s ",
                httpResponseStartLine.getHttpVersion(),
                httpResponseStartLine.getStatusCode().getCode(),
                httpResponseStartLine.getStatusCode().getText()
        );
    }

    private String generateHeaders() {
        return headers.getEntrySet()
                .stream()
                .map(header -> String.format("%s: %s ", header.getKey(), header.getValue()))
                .collect(Collectors.joining(NEW_LINE));
    }

    public void sendRedirect(String location) {
        httpResponseStartLine = new HttpResponseStartLine("HTTP/1.1", StatusCode.FOUND);
        headers.add("Location", location);
    }

    public void addCookie(String key, String value) {
        headers.add("Set-Cookie", key + "=" + value);
    }
}
