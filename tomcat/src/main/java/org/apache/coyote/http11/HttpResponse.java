package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpResponse {

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css;charset=utf-8",
            "js", "text/javascript",
            "svg", "image/svg+xml"
    );

    private final OutputStream outputStream;
    private final List<String> headers = new ArrayList<>();
    private String statusLine;
    private byte[] body = new byte[0];

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setStatus(final String statusLine) {
        this.statusLine = statusLine;
    }

    public void addHeader(final String name, final String value) {
        headers.add(name + ": " + value);
    }

    public void addCookie(final String name, final String value) {
        addHeader("Set-Cookie", name + "=" + value);
    }

    public void setBody(final byte[] body) {
        this.body = body;
    }

    public void sendRedirect(final String location) throws IOException {
        setStatus("302 Found");
        addHeader("Location", location);
        setBody(new byte[0]);
        send();
    }

    public void sendMethodNotAllowed(final List<String> allowedMethods) throws IOException {
        setStatus("405 Method Not Allowed");
        addHeader("Allow", String.join(", ", allowedMethods));
        setBody(new byte[0]);
        send();
    }

    public void forward(final String path) throws IOException {
        final URL resource = findResource(path);
        if (resource == null) {
            setStatus("404 Not Found");
            addHeader("Content-Type", "text/html;charset=utf-8");
            setBody(readResource("/404.html"));
            send();
            return;
        }

        setStatus("200 OK");
        addHeader("Content-Type", determineContentType(path));
        setBody(readResource(resource));
        send();
    }

    private URL findResource(final String path) {
        return getClass().getClassLoader().getResource("static" + path);
    }

    private byte[] readResource(final String path) throws IOException {
        final URL resource = findResource(path);
        if (resource == null) {
            throw new IOException("정적 파일을 찾을 수 없습니다: " + path);
        }
        return readResource(resource);
    }

    private byte[] readResource(final URL resource) throws IOException {
        try (final InputStream inputStream = resource.openStream()) {
            return inputStream.readAllBytes();
        }
    }

    private String determineContentType(final String path) {
        final int extensionIndex = path.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == path.length() - 1) {
            return "application/octet-stream";
        }
        final String extension = path.substring(extensionIndex + 1).toLowerCase(Locale.ROOT);
        return CONTENT_TYPES.getOrDefault(extension, "application/octet-stream");
    }

    public void send() throws IOException {
        final StringBuilder head = new StringBuilder();
        head.append("HTTP/1.1 ").append(statusLine).append("\r\n");
        for (String header : headers) {
            head.append(header).append("\r\n");
        }
        head.append("Content-Length: ").append(body.length).append("\r\n");
        head.append("\r\n");

        outputStream.write(head.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
