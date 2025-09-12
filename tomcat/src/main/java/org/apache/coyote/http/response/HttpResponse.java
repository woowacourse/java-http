package org.apache.coyote.http.response;

import jakarta.servlet.http.Cookie;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(final ResponseLine responseLine, final Map<String, String> headers, final String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(final String responseBody, final String contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        return new HttpResponse(ResponseLine.ok(), headers, responseBody);
    }

    public static HttpResponse redirection(final String location, final String contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        headers.put("Content-Type", contentType);
        return new HttpResponse(ResponseLine.found(), headers, "");
    }

    public static HttpResponse redirectionWithCookie(final String location, final String contentType,
                                                     final Cookie cookie) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        headers.put("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
        headers.put("Content-Type", contentType);
        return new HttpResponse(ResponseLine.found(), headers, "");
    }

    public static HttpResponse unAuthentication(final String responseBody, final String contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        return new HttpResponse(ResponseLine.unauthorized(), headers, responseBody);
    }

    public String writeTo() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(responseLine.toString()).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n");
        responseBuilder.append(body);

        return responseBuilder.toString();
    }

    public String getBody() {
        return body;
    }
}
