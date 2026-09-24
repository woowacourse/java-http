package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class HttpResponse {
    private static final String VERSION = "HTTP/1.1";
    private final HttpHeaders headers;

    private byte[] body;
    private HttpStatus status;
    private Cookies cookies = Cookies.empty();

    public HttpResponse() {
        this(HttpStatus.OK, HttpHeaders.empty());
    }

    public HttpResponse(HttpStatus status, HttpHeaders headers) {
        this(status, headers, new byte[0]);
    }

    public HttpResponse(HttpStatus status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public void setCookies(Cookies cookies) {
        this.cookies = cookies;
    }

    public byte[] toHttpBytes() {
        String responseLine = String.join(" ",
                VERSION, String.valueOf(status.getCode()), status.getMessage());
        StringBuilder headBuilder = new StringBuilder();
        String head = responseLine + " \r\n" + headers.toMessage();
        headBuilder.append(head);
        for (Cookie cookie : cookies.values()) {
            headBuilder.append("Set-Cookie: ").append(cookie.toHeaderValue()).append(" \r\n");
        }
        headBuilder.append("\r\n");
        byte[] headBytes = headBuilder.toString().getBytes(StandardCharsets.ISO_8859_1);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.writeBytes(headBytes);
        outputStream.writeBytes(body);
        return outputStream.toByteArray();
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public void addCookie(Cookie cookie) {
        this.cookies = cookies.add(cookie);
    }

    public void setError(HttpStatus status) {
        if (!status.isError()) {
            throw new IllegalArgumentException("Http 상태 코드가 에러 상태여야 합니다.");
        }
        this.status = status;
        setBody(MimeType.TEXT_HTML, new byte[0]);
    }

    public void setBody(MimeType mimeType, byte[] body) {
        headers.addContentType(mimeType);
        headers.addContentLength(body.length);
        this.body = body;
    }

    public void setRedirect(String path, Cookies cookies) {
        this.status = HttpStatus.FOUND;
        this.headers.add("Location", path);
        this.headers.addContentLength(0);
        this.cookies = cookies;
    }

    public void setRedirect(String path) {
        setRedirect(path, Cookies.empty());
    }
}
