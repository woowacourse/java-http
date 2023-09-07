package org.apache.coyote.http11.response;

import java.util.Objects;
import org.apache.coyote.http11.resource.Cookie;

public class HttpResponseParser {

    protected HttpResponseParser() {
    }

    public static byte[] parseToBytes(final HttpResponse httpResponse) {
        final StringBuilder response = new StringBuilder();

        responseStatus(httpResponse, response);
        contentType(httpResponse, response);
        charset(httpResponse, response);

        cookies(httpResponse, response);
        contentLength(httpResponse, response);
        responseBody(httpResponse, response);

        return response.toString().getBytes();
    }

    protected static void responseBody(final HttpResponse httpResponse, final StringBuilder response) {
        if (Objects.nonNull(httpResponse.responseBody)) {
            response.append(httpResponse.responseBody);
        }
    }

    protected static void contentLength(final HttpResponse httpResponse, final StringBuilder response) {
        response.append("Content-Length: ")
                .append(httpResponse.contentLength)
                .append(" ")
                .append(System.lineSeparator());
        response.append(System.lineSeparator());
    }

    protected static void cookies(final HttpResponse httpResponse, final StringBuilder response) {
        for (Cookie cookie : httpResponse.cookies) {
            response.append("Set-Cookie: ")
                    .append(cookie.getKey())
                    .append("=")
                    .append(cookie.getValue())
                    .append(" ")
                    .append(System.lineSeparator());
        }
    }

    protected static void responseStatus(final HttpResponse httpResponse, final StringBuilder response) {
        response.append("HTTP/1.1 ")
                .append(httpResponse.responseStatus)
                .append(" ")
                .append(System.lineSeparator());
    }

    protected static void charset(final HttpResponse httpResponse, final StringBuilder response) {
        if (Objects.nonNull(httpResponse.charSet)) {
            response
                    .append(";charset=")
                    .append(httpResponse.charSet)
                    .append(" ");
        }
        response.append(System.lineSeparator());
    }

    protected static void header(final String headerName, final String headerValue, final StringBuilder response) {
        if (Objects.nonNull(headerName) && Objects.nonNull(headerValue)) {
            response.append(headerName).append(": ")
                    .append(headerValue)
                    .append(" ");
        }
        response.append(System.lineSeparator());
    }

    protected static void contentType(final HttpResponse httpResponse, final StringBuilder response) {
        if (Objects.nonNull(httpResponse.contentType)) {
            response.append("Content-Type: ")
                    .append(httpResponse.contentType);
        }
    }
}
