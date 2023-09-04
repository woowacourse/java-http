package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String SET_COOKIE = "Set-Cookie";

    private final StatusCode statusCode;
    private final String contentType;
    private final String responseBody;

    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(final StatusCode statusCode, final String contentType, final String responseBody) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public byte[] toBytes() {
        String responseHeader = String.join(DELIMITER,
                "HTTP/1.1 " + statusCode.getValue() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ");

        for (String headerName : headers.keySet()) {
            String headerInfo = headerName + ": " + headers.get(headerName) + " ";
            responseHeader = String.join(DELIMITER, responseHeader, headerInfo);
        }

        return String.join(DELIMITER, responseHeader, "", responseBody).getBytes();
    }

    public static HttpResponse toNotFound() {
        return new HttpResponse(StatusCode.NOT_FOUND, ContentType.TEXT_HTML.getValue(), ViewLoader.toNotFound());
    }

    public static HttpResponse toUnauthorized() {
        return new HttpResponse(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML.getValue(), ViewLoader.toUnauthorized());
    }

    public void sendRedirect(final String redirectUrl) {
        headers.put("Location", redirectUrl);
    }

    public void addCookie(String sessionId) {
        headers.put(SET_COOKIE, "JSESSIONID=" + sessionId);
    }
}
