package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 응답의 상태, 헤더, 본문을 담아두고 최종 HTTP 응답 메시지 문자열로 조립함.
 */
public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String BLANK = " ";
    private static final String CRLF = "\r\n";

    private HttpStatus status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";

    public HttpResponse() {
    }

    public void ok(String contentType, String body) {
        setResponse(HttpStatus.OK, contentType, body);
    }

    public void notFound(String contentType, String body) {
        setResponse(HttpStatus.NOT_FOUND, contentType, body);
    }

    public void sendRedirect(String location) {
        this.status = HttpStatus.FOUND;
        this.body = "";
        addHeader("Location", location);
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

    private void setResponse(HttpStatus status, String contentType, String body) {
        this.status = status;
        this.body = body;
        addHeader("Content-Type", contentType);
        addHeader("Content-Length", String.valueOf(body.getBytes().length));
    }
}
