package org.apache.coyote.http11.parser;

import java.util.stream.Collectors;
import org.apache.coyote.http11.data.HttpResponse;

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
        String cookies = httpResponse.getHttpCookies().stream()
                .map(HttpCookieParser::formatCookieForResponse)
                .map(cookie -> "Set-Cookie: " + cookie + "\r\n")
                .collect(Collectors.joining());
        if (!cookies.isEmpty()) {
            headerBuilder.append(cookies).append(" \r\n");
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
