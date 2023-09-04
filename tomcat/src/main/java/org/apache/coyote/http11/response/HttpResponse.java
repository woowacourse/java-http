package org.apache.coyote.http11.response;

import java.io.IOException;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String EMPTY_LINE = "";
    private static final String NEW_LINE = "\r\n";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    private final HttpResponseHeaders headers;
    private HttpResponseStartLine httpResponseStartLine;
    private String responseBody;

    public HttpResponse() {
        this.headers = HttpResponseHeaders.empty();
    }

    public void sendRedirect(final String location) {
        httpResponseStartLine = new HttpResponseStartLine(DEFAULT_HTTP_VERSION, StatusCode.FOUND);
        headers.add("Location", location);
    }

    //FIXME: 쿠키를 여러개 설정할 수 있도록 수정
    public void addCookie(final String key, final String value) {
        headers.add("Set-Cookie", key + KEY_VALUE_DELIMITER + value);
    }

    public void addHeader(final String name, final String value) {
        headers.add(name, value);
    }

    public boolean isNotRedirect(){
        return httpResponseStartLine==null || httpResponseStartLine.getStatusCode()!=StatusCode.FOUND;
    }

    public void setHttpResponseStartLine(final StatusCode statusCode) {
        httpResponseStartLine = new HttpResponseStartLine(DEFAULT_HTTP_VERSION, statusCode);
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
}
