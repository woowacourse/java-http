package org.apache.catalina.http;

import static org.apache.catalina.http.vo.Mime.HTML;
import static org.apache.catalina.http.vo.Mime.JSON;

import org.apache.catalina.http.vo.HttpStatus;
import org.apache.catalina.storage.Cookie;
import org.apache.coyote.http11.util.FileReader;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private OutputStream outputStream;

    private final String version;
    private HttpStatus status;
    private final Map<String, String> headers;
    private String body;

    private HttpResponse(OutputStream outputStream, String version, HttpStatus status, Map<String, String> headers, String body) {
        this.outputStream = outputStream;
        this.version = version;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    private HttpResponse(String version, HttpStatus status, Map<String, String> headers, String body) {
        this(null, version, status, headers, body);
    }

    public static HttpResponse of(HttpStatus status, String body) {
        return new HttpResponse(DEFAULT_VERSION, status, new HashMap<>(), body);
    }

    public static HttpResponse empty(OutputStream outputStream) {
        return new HttpResponse(outputStream, DEFAULT_VERSION, null, new HashMap<>(), "");
    }

    // 2XX
    public static HttpResponse ok(String body) {
        return new HttpResponse(DEFAULT_VERSION, HttpStatus.OK, new HashMap<>(), body);
    }

    // 4XX
    public static HttpResponse unauthorized() throws IOException {
        final var response = new HttpResponse(DEFAULT_VERSION, HttpStatus.UNAUTHORIZED, new HashMap<>(), FileReader.readByName("401.html"));
        response.setContentType(HTML.getType());
        return response;
    }
    public static HttpResponse notFound() throws IOException {
        final var response = new HttpResponse(DEFAULT_VERSION, HttpStatus.UNAUTHORIZED, new HashMap<>(), FileReader.readByName("404.html"));
        response.setContentType(HTML.getType());
        return response;
    }

    // 5XX
    public static HttpResponse internalServerError() throws IOException {
        final var response = new HttpResponse(DEFAULT_VERSION, HttpStatus.UNAUTHORIZED, new HashMap<>(), FileReader.readByName("500.html"));
        response.setContentType(HTML.getType());
        return response;
    }

    public void sendError(HttpStatus status) throws IOException {
        setStatus(status);
        setContentType(JSON.getType());
        setBody(""); // TODO: exception body format
        send();
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send() throws IOException {
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

    public int getStatusCode() {
        return status.value();
    }

    public String getStatusReason() {
        return status.reason();
    }

    public String getStatusLine() {
        return String.format("%s %d %s ", version, getStatusCode(), getStatusReason());
    }

    public String getHeaderString() {
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

    public String getBody() {
        return body;
    }

    public String join() {
        return String.join("\r\n",
                getStatusLine(),
                getHeaderString(),
                getBody()
        );
    }
}
