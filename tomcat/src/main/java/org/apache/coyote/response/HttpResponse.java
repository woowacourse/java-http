package org.apache.coyote.response;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;
    private final Location location;
    private final Cookie cookie;

    private HttpResponse(StatusCode statusCode, ContentType contentType, String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = null;
        this.cookie = Cookie.empty();
    }

    private HttpResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody,
                        final Location location) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = location;
        this.cookie = Cookie.empty();
    }

    private HttpResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody,
                        final Location location, final Cookie cookie) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = location;
        this.cookie = cookie;
    }

    public static HttpResponse of(final StatusCode statusCode, final ContentType contentType, final String requestUrl) {
        final String responseBody = new String(Objects.requireNonNull(readAllFile(requestUrl)), UTF_8);
        return new HttpResponse(statusCode, contentType, responseBody);
    }

    public static HttpResponse of(final StatusCode statusCode, final ContentType contentType, final Location location) {
        final String responseBody = new String(Objects.requireNonNull(readAllFile(location.toString())), UTF_8);
        return new HttpResponse(statusCode, contentType, responseBody, location);
    }

    public static HttpResponse of(final StatusCode statusCode, final ContentType contentType, final Location location,
                                  final Cookie cookie) {
        final String responseBody = new String(Objects.requireNonNull(readAllFile(location.toString())), UTF_8);
        return new HttpResponse(statusCode, contentType, responseBody, location, cookie);
    }

    public String getResponse() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ").append(statusCode).append(" \r\n");
        builder.append("Content-Type: ").append(contentType).append(";charset=utf-8 \r\n");
        builder.append("Content-Length: ").append(responseBody.getBytes().length).append(" \r\n");

        appendLocation(builder);
        appendSetCookie(builder);

        builder.append("\r\n");
        builder.append(responseBody);

        return builder.toString();
    }

    public Cookie getCookie() {
        return cookie;
    }

    private void appendLocation(StringBuilder builder) {
        if (location != null) {
            builder.append("Location: ").append(location).append(" \r\n");
        }
    }

    private void appendSetCookie(StringBuilder builder) {
        if (!cookie.isEmpty()) {
            builder.append("Set-Cookie: ").append(cookie.toHeaderFormat()).append(" \r\n");
        }
    }

    private static byte[] readAllFile(final String requestUrl) {
        final URL resourceUrl = ClassLoader.getSystemResource("static" + requestUrl);
        final Path path = new File(resourceUrl.getPath()).toPath();

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("파일을 읽어들이지 못했습니다.");
            return null;
        }
    }
}
