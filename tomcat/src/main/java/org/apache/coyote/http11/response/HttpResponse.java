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
    private StatusCode statusCode;
    private ContentType contentType;
    private String responseBody;
    private final Map<String, String> otherHeader = new HashMap<>();

    private Cookie cookie;

    public HttpResponse(final String version) {
        this(version, null, null, null);
    }

    public HttpResponse(final String version, final StatusCode statusCode, final ContentType contentType, final String responseBody) {
        this.version = version;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
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

    public void setStatusCode(final StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }

    public void setResponseBodyByUrl(final URL url) throws IOException {
        this.responseBody = makeResponseBody(url);
    }

    private String makeResponseBody(final URL resource) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }
}
