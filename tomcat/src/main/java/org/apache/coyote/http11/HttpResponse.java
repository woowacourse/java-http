package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final List<String> cookies = new ArrayList<>();
    private String body = "";

    public void setBody(String contentType, String body) {
        this.status = HttpStatus.OK;
        this.body = body;
        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void setStaticResource(String path) throws IOException {
        setBody(contentTypeOf(path), resolveContentOf(path));
    }

    private String resolveContentOf(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        if (!path.equals("/") && resource != null) {
            return Files.readString(new File(resource.getFile()).toPath());
        }
        return "Hello world!";
    }

    private String contentTypeOf(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

    public void sendRedirect(String location) {
        this.status = HttpStatus.FOUND;
        headers.put("Location", location);
    }

    public void addCookie(String name, String value) {
        cookies.add(name + "=" + value);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public byte[] getBytes() {
        StringBuilder message = new StringBuilder();
        message.append(VERSION).append(" ")
                .append(status.getCode()).append(" ")
                .append(status.getReasonPhrase()).append(" ").append(CRLF);
        cookies.forEach(cookie -> message.append("Set-Cookie: ").append(cookie).append(" ").append(CRLF));
        headers.forEach((name, value) -> message.append(name).append(": ").append(value).append(" ").append(CRLF));
        message.append(CRLF).append(body);
        return message.toString().getBytes(StandardCharsets.UTF_8);
    }
}
