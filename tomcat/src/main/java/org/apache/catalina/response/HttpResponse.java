package org.apache.catalina.response;

import org.apache.catalina.Cookie;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private StatusLine statusLine;
    private final Map<Header, Object> headers = new LinkedHashMap<>();
    private String body;

    public void setStatusLine(Status status) {
        this.statusLine = new StatusLine(status);
    }

    public void setCookie(Cookie cookie) {
        headers.put(Header.SET_COOKIE, cookie);
    }

    public void setContentType(ContentType contentType) {
        headers.put(Header.CONTENT_TYPE, contentType);
    }

    public void forward(String path) {
        setStatusLine(Status.OK);
        setContentType(ContentType.of(path));
        String body = getBody(path);
        setBody(body);
    }

    public void sendRedirect(String path) {
        setLocation(path);
        setContentType(ContentType.of(path));
        String body = getBody(path);
        setBody(body);
    }

    public void setBody(String body) {
        this.body = body;
        setContentLength(body.getBytes().length);
    }

    private void setContentLength(int contentLength) {
        headers.put(Header.CONTENT_LENGTH, contentLength);
    }

    private void setLocation(String location) {
        headers.put(Header.LOCATION, location);
    }

    public Map<Header, Object> getHeaders() {
        return headers;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public String getBody() {
        return body;
    }

    private String getBody(String path) {
        int extension = path.lastIndexOf(".");
        path = "static" + path;
        if (extension == -1) {
            path += ".html";
        }
        return readFile(path);
    }

    private String readFile(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) {
            return readFile("static/404.html");
        }
        File file = new File(url.getFile());
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes);
        } catch (IOException e) {
            return readFile("static/500.html");
        }
    }
}
