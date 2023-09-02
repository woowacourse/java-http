package org.apache.coyote.common;

import static java.util.stream.Collectors.joining;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpResponse {

    private final HttpProtocol httpProtocol;
    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private String contentBody = "";

    public HttpResponse(HttpProtocol httpProtocol, HttpStatus httpStatus, HttpHeaders httpHeaders) {
        this.httpProtocol = httpProtocol;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
    }

    public void setContentBody(String body) {
        this.contentBody = body;
        httpHeaders.setHeader("Content-Length", String.valueOf(body.getBytes().length));
    }

    public byte[] toBytes() {
        String firstLine = getFirstLine();
        String header = getHeader();
        return String.join(System.lineSeparator(), firstLine, header, "", contentBody)
            .getBytes(StandardCharsets.UTF_8);
    }

    private String getFirstLine() {
        return String.join(" ",
            httpProtocol.getValue(),
            String.valueOf(httpStatus.getStatusCode()),
            httpStatus.toString()) + " ";
    }

    private String getHeader() {
        return httpHeaders.getHeaders().entrySet().stream()
            .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()) + " ")
            .collect(joining(System.lineSeparator()));
    }

    public void addHeader(String key, String value) {
        httpHeaders.addHeader(key, value);
    }

    public void addHeader(String key, List<String> values) {
        httpHeaders.addHeader(key, values);
    }

    public void setHeader(String key, String value) {
        httpHeaders.setHeader(key, value);
    }

    public void setHeader(String key, List<String> values) {
        httpHeaders.setHeader(key, values);
    }

    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getContentBody() {
        return contentBody;
    }
}
