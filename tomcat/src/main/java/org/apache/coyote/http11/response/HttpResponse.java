package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(byte[] body, String contentType) {
        if (contentType != null) {
            addHeader(CONTENT_TYPE, contentType + CHARSET_UTF_8);
        }
        this.body = body;
    }

    public void setBody(String body, String contentType) {
        setBody(body.getBytes(StandardCharsets.UTF_8), contentType);
    }

    public void sendRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        addHeader(LOCATION, location);
    }

    public void addCookie(String name, String value) {
        addHeader(SET_COOKIE, name + "=" + value + "; Path=/; HttpOnly");
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(getBytes());
        outputStream.flush();
    }

    public byte[] getBytes() {
        StringBuilder head = new StringBuilder();
        head.append(HTTP_VERSION).append(" ")
            .append(status.getCode()).append(" ")
            .append(status.getReasonPhrase()).append(" ").append(CRLF);
        headers.forEach((name, value) ->
            head.append(name).append(": ").append(value).append(" ").append(CRLF));
        head.append(CONTENT_LENGTH).append(": ").append(body.length).append(" ").append(CRLF);
        head.append(CRLF);

        byte[] headBytes = head.toString().getBytes(StandardCharsets.UTF_8);
        byte[] response = new byte[headBytes.length + body.length];
        System.arraycopy(headBytes, 0, response, 0, headBytes.length);
        System.arraycopy(body, 0, response, headBytes.length, body.length);
        return response;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public byte[] getBody() {
        return body;
    }
}
