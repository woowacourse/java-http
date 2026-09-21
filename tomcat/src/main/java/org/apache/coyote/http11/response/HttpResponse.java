package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.request.HttpCookie;

public class HttpResponse {
    private static final String VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String CHARSET = ";charset=utf-8";
    private static final String SET_COOKIE = "Set-Cookie";

    private final HttpStatus status;
    private final Map<String, String> headers;
    private final HttpCookie cookie;
    private final String body;

    public HttpResponse(HttpStatus status, Map<String, String> headers, String body) {
        this.status = status;
        this.headers = new LinkedHashMap<>(headers);
        this.cookie = HttpCookie.empty();
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, String contentType, String body) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType + CHARSET);

        return new HttpResponse(status, headers, body);
    }

    public static HttpResponse of(HttpStatus status, StaticResource staticResource) {
        return of(status, staticResource.getContentType(), staticResource.getBody());
    }

    public void addCookie(String name, String value) {
        cookie.add(name, value);
    }

    public byte[] toBytes() {
        List<String> lines = new ArrayList<>();
        lines.add(VERSION + " " + status.getCode() + " " + status.getReasonPhrase() + " ");

        headers.forEach((name, value) -> lines.add(name + ": " + value + " "));
        cookie.toHeaderValues().forEach(value -> lines.add(SET_COOKIE + ": " + value + " "));
        lines.add("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ");

        lines.add("");
        lines.add(body);

        return String.join(CRLF, lines).getBytes(StandardCharsets.UTF_8);
    }
}
