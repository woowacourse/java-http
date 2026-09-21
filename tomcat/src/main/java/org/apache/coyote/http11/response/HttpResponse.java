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
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers = new LinkedHashMap<>();
    private HttpCookie cookie = HttpCookie.empty();
    private HttpStatus status = HttpStatus.OK;
    private String body = "";

    public static HttpResponse of(HttpStatus status, String contentType, String body) {
        HttpResponse response = new HttpResponse();
        response.setStatus(status);
        response.setBody(contentType, body);

        return response;
    }

    public static HttpResponse of(HttpStatus status, StaticResource staticResource) {
        return of(status, staticResource.getContentType(), staticResource.getBody());
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(String contentType, String body) {
        setHeader(CONTENT_TYPE, contentType + CHARSET);
        this.body = body;
    }

    public void setStaticResource(HttpStatus status, StaticResource staticResource) {
        setStatus(status);
        setBody(staticResource.getContentType(), staticResource.getBody());
    }

    public void sendRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        setHeader(LOCATION, location);
    }

    public void addCookie(String name, String value) {
        cookie.add(name, value);
    }

    public void reset() {
        headers.clear();
        cookie = HttpCookie.empty();
        status = HttpStatus.OK;
        body = "";
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
