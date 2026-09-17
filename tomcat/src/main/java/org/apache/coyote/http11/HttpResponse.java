package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private String startLine = "HTTP/1.1 200 OK \r\n";
    private HttpResponseHeader header;
    private HttpResponseBody body;

    public HttpResponse() {
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public void setHeader(HttpResponseHeader header) {
        this.header = header;
    }

    public void setBody(HttpResponseBody body) {
        this.body = body;
    }

    public void sendRedirect(String location) {
        this.startLine = "HTTP/1.1 302 Found \r\n";
        if (header != null) {
            header.setHeaders("Location", location);
        }
    }

    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            header.addCookie(cookie);
        }
    }

    public byte[] getResponseBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(startLine.getBytes(StandardCharsets.UTF_8));
        if (header != null) {
            out.write(header.getResponseHeaderString().getBytes(StandardCharsets.UTF_8));
        }
        if (body != null) {
            out.write(body.getBytes()); // HttpResponseBody가 byte[] getBytes()를 제공
        }
        return out.toByteArray();
    }

    public boolean hasCookie(String key) {
        return header.hasCookie(key);
    }
}
