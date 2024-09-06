package org.apache.coyote.http11;

import java.util.stream.Collectors;

public class HttpResponseParser {

    public static String parse(HttpResponse httpResponse) {
        StringBuilder headerBuilder = new StringBuilder(httpResponse.getHttpVersion().getValue()).append(" ")
                .append(httpResponse.getHttpStatusCode().getString()).append(" \r\n");
        if (httpResponse.getContentType() != null) {
            headerBuilder.append("Content-Type: ")
                    .append(httpResponse.getContentType().getString()).append(" \r\n");
        }
        if (httpResponse.getResponseBody() != null) {
            headerBuilder.append("Content-Length: ")
                    .append(httpResponse.getResponseBody().getBytes().length).append(" \r\n");
        }
        String cookies = httpResponse.getResponseCookies().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
        if (!cookies.isEmpty()) {
            headerBuilder.append("Set-Cookie: ").append(cookies).append(" \r\n");
        }
        if (httpResponse.getRedirectUrl() != null) {
            headerBuilder.append("Location: ").append(httpResponse.getRedirectUrl()).append(" \r\n");
        }
        if (httpResponse.getResponseBody() == null) {
            return headerBuilder.toString();
        }
        return headerBuilder + "\r\n" + httpResponse.getResponseBody();
    }
}
