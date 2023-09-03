package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpHeaders;

public class HttpResponse {

    public static final String EMPTY_LINE = "";
    public static final String NEW_LINE = "\r\n";
    private HttpResponseStartLine httpResponseStartLine;
    private HttpHeaders headers;
    private String body;

    public HttpResponse() {
        this.headers = HttpHeaders.empty();
    }

    public void setHttpResponseStartLine(String httpVersion, StatusCode statusCode) {
        httpResponseStartLine = new HttpResponseStartLine(httpVersion, statusCode);
    }

    public void setHttpResponseStartLine(StatusCode statusCode) {
        httpResponseStartLine = new HttpResponseStartLine("HTTP/1.1", statusCode);
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void setBody(final byte[] body) {
        headers.add("Content-Length", String.valueOf(body.length));
        this.body = new String(body);
    }

    public byte[] generateResponse() throws IOException {
        return String.join(NEW_LINE,
                generateStartLine(),
                generateHeaders(),
                EMPTY_LINE,
                body).getBytes();
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
}
