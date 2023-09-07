package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String version;
    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;
    private final Map<String, String> otherHeader = new HashMap<>();

    private Cookie cookie;

    public HttpResponse(final String version, final StatusCode statusCode, final ContentType contentType, final String responseBody) {
        this.version = version;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse createBy(final String version, final URL resource, final StatusCode statusCode) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        final String responseBody = new String(fileBytes, StandardCharsets.UTF_8);
        return new HttpResponse(version, statusCode, ContentType.findByPath(resource.getPath()), responseBody);
    }

    public void addCookie(final String cookie) {
        this.cookie = new Cookie(cookie);
    }

    public boolean containJsessionId() {
        return cookie != null && cookie.containsJsessionId();
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Map<String, String> getOtherHeader() {
        return otherHeader;
    }

    public Cookie getCookie() {
        return cookie;
    }

}
