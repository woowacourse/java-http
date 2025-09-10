package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Session;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private StatusLine statusLine;
    private ContentType contentType;
    private String location;
    private List<HttpCookie> httpCookies;
    private long contentLength;
    private byte[] body;

    public HttpResponse(ProtocolVersion protocolVersion,
                        ResponseStatus responseStatus,
                        ContentType contentType,
                        String location,
                        long contentLength,
                        byte[] body) {
        this.statusLine = new StatusLine(protocolVersion, responseStatus);
        this.contentType = contentType;
        this.location = location;
        this.httpCookies = new ArrayList<>();
        this.contentLength = contentLength;
        this.body = body;
    }

    public static HttpResponse empty() {
        return new HttpResponse(null, null, null, null, 0, new byte[0]);
    }

    public static HttpResponse of(ResponseStatus responseStatus, ContentType contentType, byte[] body) {
        return new HttpResponse(
                ProtocolVersion.HTTP11,
                responseStatus,
                contentType,
                null,
                body.length,
                body);
    }

    public void setDefaultResponse(ResponseStatus responseStatus, ContentType contentType, byte[] body) {
        this.statusLine = new StatusLine(ProtocolVersion.HTTP11, responseStatus);
        this.contentType = contentType;
        this.body = body;
        this.contentLength = body.length;
    }

    public void sendRedirect(ResponseStatus responseStatus, String location) {
        this.statusLine = new StatusLine(ProtocolVersion.HTTP11, responseStatus);
        this.location = location;
        this.contentType = ContentType.HTML;
        this.contentLength = 0;
        this.body = new byte[0];
    }

    public void setCookie(String key, String value) {
        httpCookies.add(new HttpCookie(key, value));
    }

    public byte[] convertToBytes() {
        final var headers = buildHeaders();
        final var bodyString = new String(body, StandardCharsets.UTF_8);
        final String fullResponse = headers + bodyString;
        return fullResponse.getBytes();
    }

    public void setSession(Session session) {
        httpCookies.add(new HttpCookie("JSESSIONID", session.getId()));
    }

    private String buildHeaders() {
        StringBuilder sb = new StringBuilder();
        sb.append(statusLine.convertToResponseLine()).append(CRLF);
        if (location != null && !location.isBlank()) {
            sb.append("Location: ").append(location).append(CRLF);
        }
        if (contentType != null) {
            sb.append(buildContentTypeHeader()).append(CRLF);
        }
        if (!httpCookies.isEmpty()) {
            for (HttpCookie httpCookie : httpCookies) {
                sb.append("Set-Cookie: ")
                        .append(httpCookie.getKey()).append("=").append(httpCookie.getValue())
                        .append(CRLF);
            }
        }
        sb.append("Content-Length: ").append(contentLength).append(CRLF);
        sb.append(CRLF);
        return sb.toString();
    }

    private String buildContentTypeHeader() {
        if (contentType == ContentType.HTML || contentType == ContentType.JS || contentType == ContentType.CSS) {
            return "Content-Type: " + contentType.getFirstMimeType() + ";charset=utf-8";
        }
        return "Content-Type: " + contentType.getFirstMimeType();
    }
}
