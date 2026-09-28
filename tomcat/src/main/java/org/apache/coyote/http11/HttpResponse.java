package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class HttpResponse {

    private static final String STATIC_ROOT = "static";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final OutputStream outputStream;

    private String sessionCookie;

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addCookie(final String cookie) {
        sessionCookie = cookie;
    }

    public void send(final String contentType, final byte[] body) throws IOException {
        final var headers = "HTTP/1.1 200 OK \r\n"
                + createSetCookieHeader()
                + String.join("\r\n",
                        "Content-Type: " + contentType + " ",
                        "Content-Length: " + body.length + " ",
                        "",
                        ""
                );

        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendRedirect(final String location) throws IOException {
        final var response = "HTTP/1.1 302 Found\r\n"
                + createSetCookieHeader()
                + String.join("\r\n",
                        "Location: " + location,
                        "Content-Length: 0",
                        "",
                        ""
                );

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public void sendStaticResource(final String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            send(HTML_CONTENT_TYPE, "Hello world!".getBytes(StandardCharsets.UTF_8));
            return;
        }

        validateRequestPath(requestPath);

        final var resourcePath = STATIC_ROOT + requestPath;
        final var classLoader = HttpResponse.class.getClassLoader();

        try (final var resource = classLoader.getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("리소스를 찾을 수 없습니다: " + resourcePath);
            }

            send(findContentType(requestPath), resource.readAllBytes());
        }
    }

    private void validateRequestPath(final String requestPath) {
        final var pathSegments = List.of(requestPath.split("/"));

        if (!requestPath.startsWith("/") || pathSegments.contains("..")) {
            throw new UncheckedServletException(
                    new IllegalArgumentException("유효하지 않은 요청 경로입니다: " + requestPath)
            );
        }
    }

    private String findContentType(final String requestPath) {
        final var contentType = URLConnection.guessContentTypeFromName(requestPath);

        if ("text/html".equals(contentType)) {
            return HTML_CONTENT_TYPE;
        }

        if (contentType == null) {
            return DEFAULT_CONTENT_TYPE;
        }

        return contentType;
    }

    private String createSetCookieHeader() {
        if (sessionCookie == null) {
            return "";
        }

        return "Set-Cookie: " + sessionCookie + "\r\n";
    }
}
