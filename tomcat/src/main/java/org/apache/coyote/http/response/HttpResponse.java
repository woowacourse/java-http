package org.apache.coyote.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.coyote.http.cookie.HttpCookie;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(ResponseLine responseLine, Map<String, String> headers, String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(String body, String contentType) {
        Map<String, String> headers = Map.of(
                "Content-Type", contentType + ";charset=utf-8",
                "Content-Length", String.valueOf(body.getBytes().length)
        );
        return new HttpResponse(ResponseLine.ok(), headers, body);
    }

    public static HttpResponse okWithCookie(String body, String contentType, String cookieName, String cookieValue) {
        Map<String, String> headers = Map.of(
                "Content-Type", contentType + ";charset=utf-8",
                "Content-Length", String.valueOf(body.getBytes().length),
                "Set-Cookie", HttpCookie.createSetCookieHeader(cookieName, cookieValue)
        );
        return new HttpResponse(ResponseLine.ok(), headers, body);
    }
    
    public static HttpResponse redirect(String location) {
        return new HttpResponse(ResponseLine.found(), Map.of("Location", location), "");
    }

    public static HttpResponse redirectWithCookie(String location, String cookieName, String cookieValue) {
        Map<String, String> headers = Map.of(
                "Location", location,
                "Set-Cookie", HttpCookie.createSetCookieHeader(cookieName, cookieValue)
        );
        return new HttpResponse(ResponseLine.found(), headers, "");
    }

    public static HttpResponse unauthorized(String body) {
        Map<String, String> headers = Map.of(
                "Content-Type", "text/html;charset=utf-8",
                "Content-Length", String.valueOf(body.getBytes().length)
        );
        return new HttpResponse(ResponseLine.unauthorized(), headers, body);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append(responseLine.toString()).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        response.append("\r\n");
        response.append(body);

        outputStream.write(response.toString().getBytes());
        outputStream.flush();
    }

}
