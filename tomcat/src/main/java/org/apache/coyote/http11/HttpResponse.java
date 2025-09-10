package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String STATUS_OK = "HTTP/1.1 200 OK ";
    private static final String STATUS_FOUND = "HTTP/1.1 302 Found ";

    private String status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;
    private final List<String> cookies = new ArrayList<>();

    public void addCookie(final String cookie) {
        cookies.add(cookie);
    }

    public void found(final String redirectLocation) {
        status = STATUS_FOUND;
        headers.put("Location", redirectLocation);
        headers.put("Content-Length", "0");
    }

    public void ok(final String path) throws IOException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource("static" + path);
        if (url == null) {
            found("/404.html");
            return;
        }
        body = new String(Files.readAllBytes(Paths.get(url.toURI())));
        status = STATUS_OK;
        headers.put("Content-Type", getContentType(path));
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void ok(final String body, final String contentType) {
        this.body = body;
        status = STATUS_OK;
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    public void write(final OutputStream outputStream) throws IOException {
        HttpResponseSerializer.write(this, outputStream);
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    public String getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<String> getCookies() {
        return cookies;
    }
}
