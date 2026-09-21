package org.apache.coyote.http11.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;

public class HttpResponse {

    private final HttpResponseHeader header;

    private String version = "HTTP/1.1";
    private HttpStatus status;
    private HttpResponseBody body;

    public HttpResponse() {
        this.header = new HttpResponseHeader(new Cookies(), new LinkedHashMap<>());
        this.status = HttpStatus.OK;
    }

    public void setHeader(String key, String value) {
        header.setHeader(key, value);
    }

    public void setBody(HttpResponseBody body) {
        this.body = body;
        header.setHeader("Content-Length", String.valueOf(body.length()));
    }

    public void sendRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        header.setHeader("Location", location);
    }

    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            header.addCookie(cookie);
        }
    }

    public byte[] getResponseBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(startLine().getBytes(StandardCharsets.UTF_8));
        out.write(header.getResponseHeaderString().getBytes(StandardCharsets.UTF_8));
        if (body != null) {
            out.write(body.getBytes());
        }
        return out.toByteArray();
    }

    public boolean hasCookie(String key) {
        return header.hasCookie(key);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String startLine() {
        return version + " " + status + "\r\n";
    }
}
