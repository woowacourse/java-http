package org.apache.coyote.http11;

import java.util.stream.Collectors;
import org.apache.catalina.Cookie;

public class Http11OutputBuffer {
    public static String parseToString(HttpResponse httpResponse) {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append(String.format("HTTP/%.1f %d %s", httpResponse.httpVersion(), httpResponse.statusCode(),
                httpResponse.status()));
        responseBuilder.append("\r\n");

        if (httpResponse.contentType() != null) {
            responseBuilder.append("Content-Type: ").append(httpResponse.contentType());
            if (httpResponse.charSet() != null) {
                responseBuilder.append(";").append(httpResponse.charSet());
            }
            responseBuilder.append("\r\n");
        }

        if (httpResponse.location() != null) {
            responseBuilder.append("Location: ").append(httpResponse.location());
            responseBuilder.append("\r\n");
        }

        responseBuilder.append("Content-Length: ").append(httpResponse.contentLength());
        responseBuilder.append("\r\n");

        if (httpResponse.cookie() != null) {
            responseBuilder.append("Set-Cookie: ").append(parseCookie(httpResponse.cookie()));
        }

        responseBuilder.append("\r\n");

        if (httpResponse.responseBody() != null) {
            responseBuilder.append(httpResponse.responseBody());
        }

        return responseBuilder.toString();
    }

    private static String parseCookie(Cookie cookie) {
        return cookie.getCookieValues().keySet().stream()
                .map(key -> key + "=" + cookie.getCookieValues().get(key))
                .collect(Collectors.joining("; "));
    }
}
