package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import java.io.BufferedReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

final class HttpRequest {
    private final String method;
    private final URI uri;
    private final QueryParameters queryParameters;
    private final QueryParameters bodyParameters;
    private final Cookie cookie;

    private HttpRequest(final String method,
                        final URI uri,
                        final QueryParameters queryParameters,
                        final QueryParameters bodyParameters,
                        final Cookie cookie) {
        this.method = method;
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.bodyParameters = bodyParameters;
        this.cookie = cookie;
    }

    static HttpRequest from(final BufferedReader reader) {
        String line;
        List<String> headerLines = new ArrayList<>();
        int contentLength = 0;
        String body = "";

        try {
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                headerLines.add(line);
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            if (contentLength > 0) {
                char[] bodyBuffer = new char[contentLength];
                reader.read(bodyBuffer, 0, contentLength);
                body = new String(bodyBuffer);
            }
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return of(headerLines, body);
    }

    static HttpRequest from(final List<String> headerLines) {
        return of(headerLines, null);
    }

    static HttpRequest of(final List<String> headers, String body) {
        final String[] requestLineParts = headers.getFirst().split(" ", 3);
        final URI uri = URI.create(requestLineParts[1]);
        final QueryParameters queryParameters = QueryParameters.from(uri.getRawQuery());

        final QueryParameters bodyParameters = QueryParameters.from(body);
        final Cookie cookie = Cookie.from(findCookieHeader(headers));

        return new HttpRequest(requestLineParts[0], uri, queryParameters, bodyParameters, cookie);
    }

    private static String findCookieHeader(final List<String> headers) {
        for (String header : headers) {
            final int separatorIndex = header.indexOf(':');
            if (separatorIndex <= 0) {
                continue;
            }

            final String name = header.substring(0, separatorIndex).trim();
            if (name.equalsIgnoreCase("Cookie")) {
                return header.substring(separatorIndex + 1).trim();
            }
        }
        return null;
    }

    String getMethod() {
        return method;
    }

    String getPath() {
        return uri.getPath();
    }

    String getParameter(final String name) {
        return queryParameters.get(name).orElse(null);
    }

    String getBodyParameter(final String name) {
        return bodyParameters.get(name).orElse(null);
    }

    Cookie getCookie() {
        return cookie;
    }
}
