package org.apache.coyote.http11;

import org.apache.catalina.storage.Cookie;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private final String version;
    private HttpStatus status;
    private final Map<String, String> headers;
    private String body;

    private HttpResponse(String version, HttpStatus status, Map<String, String> headers, String body) {
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, String body) {
        return new HttpResponse(DEFAULT_VERSION, status, new HashMap<>(), body);
    }

    public static HttpResponse empty() {
        return new HttpResponse(DEFAULT_VERSION, null, new HashMap<>(), "");
    }

    public void send(OutputStream outputStream) throws IOException {
        addHeader("Content-Length", body.getBytes().length);
        outputStream.write(join().getBytes());
        outputStream.flush();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(final String value) {
        addHeader("Content-Type", value + ";charset=utf-8");
    }

    public void setRedirect(final String url) {
        status = HttpStatus.FOUND;
        addHeader("Location", url);
    }

    public void addCookie(final Cookie cookie) {
        headers.put("Set-Cookie", cookie.toString());
    }

    public void addHeader(final String name, final Object value) {
        headers.put(name, String.valueOf(value));
    }

    public String join() {
        return String.join("\r\n",
                getStatusLine(),
                getHeaderString(),
                getBody()
        );
    }

    private String getStatusLine() {
        return String.format("%s %d %s ", version, status.code(), status.message());
    }

    private String getHeaderString() {
        if (headers.isEmpty()) {
            return "\r\n";
        }
        final var result = new StringBuilder();
        for (String key : headers.keySet()) {
            result.append(String.format("%s: %s ", key, headers.get(key)));
            result.append("\r\n");
        }
        return result.toString();
    }

    private String getBody() {
        return body;
    }
}
