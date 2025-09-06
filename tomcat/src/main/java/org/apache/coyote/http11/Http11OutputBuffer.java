package org.apache.coyote.http11;

import org.apache.catalina.ResponseCookie;

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

        if (httpResponse.contentLength() > 0) {
            responseBuilder.append("Content-Length: ").append(httpResponse.contentLength());
            responseBuilder.append("\r\n");
        }

        if (httpResponse.responseCookie() != null) {
            addCookie(httpResponse.responseCookie(), responseBuilder);
        }

        responseBuilder.append("\r\n");

        if (httpResponse.responseBody() != null) {
            responseBuilder.append(httpResponse.responseBody());
        }

        return responseBuilder.toString();
    }

    private static void addCookie(ResponseCookie responseCookie, StringBuilder responseBuilder) {
        for (String key : responseCookie.getCookieValues().keySet()) {
            responseBuilder.append("Set-Cookie: ").append(key + "=" + responseCookie.getCookieValues().get(key));
        }
    }
}
