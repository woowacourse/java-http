package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String SET_COOKIE = "Set-Cookie";

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;

    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public byte[] toBytes() {
        String responseHeader = String.join(DELIMITER,
                "HTTP/1.1 " + statusCode.getValue() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String headerInfo = entry.getKey() + ": " + entry.getValue() + " ";
            responseHeader = String.join(DELIMITER, responseHeader, headerInfo);
        }

        return String.join(DELIMITER, responseHeader, "", responseBody).getBytes();
    }

    public static HttpResponse toNotFound() {
        return new HttpResponse(StatusCode.NOT_FOUND, ContentType.TEXT_HTML, ViewLoader.toNotFound());
    }

    public static HttpResponse toUnauthorized() {
        return new HttpResponse(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML, ViewLoader.toUnauthorized());
    }

    public void sendRedirect(final String redirectUrl) {
        headers.put("Location", redirectUrl);
    }

    public void addCookie(String sessionId) {
        headers.put(SET_COOKIE, "JSESSIONID=" + sessionId);
    }
}
