package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Http11Request {

    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE_HEADER = "Cookie";

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final String protocolVersion;
    private final Map<String, String> headers;
    private final Http11Cookie cookie;
    private final String body;

    public static Http11Request create(final String rawHttpRequest) {
        final String[] headersAndBody = rawHttpRequest.split("\r\n\r\n", 2);

        final String[] headerLines = headersAndBody[0].split("\r\n");
        final String body = headersAndBody[1];

        final String[] firstLine = headerLines[0].split(" ");
        validateFirstLineSize(firstLine);

        final String method = firstLine[0];
        String path = firstLine[1];
        final String httpVersion = firstLine[2];

        final Map<String, String> queryParams = getQueryParams(path);
        if (!queryParams.isEmpty()) {
            final int queryParamStartIndex = path.indexOf("?");
            path = path.substring(0, queryParamStartIndex);
        }

        final Map<String, String> headers = getHeaders(headerLines);
        Http11Cookie cookie = null;
        if (headers.containsKey(COOKIE_HEADER)) {
            cookie = Http11Cookie.create(headers.get(COOKIE_HEADER));
            headers.remove(COOKIE_HEADER);
        }

        return new Http11Request(method, path, queryParams, httpVersion, headers, cookie, body);
    }

    private static Map<String, String> getQueryParams(final String path) {
        if (path == null || path.isEmpty()) {
            return new HashMap<>();
        }

        final String[] pathAndParams = path.split("\\?");
        if (pathAndParams.length != 2) {
            return new HashMap<>();
        }

        final String queryParams = pathAndParams[1];

        final Map<String, String> params = parseUrlEncoded(queryParams);

        return params;
    }

    private static Map<String, String> parseUrlEncoded(final String base) {
        final Map<String, String> parsed = new HashMap<>();

        final String[] pairs = base.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            parsed.put(keyValue[0], keyValue[1]);
        }
        return parsed;
    }

    private static void validateFirstLineSize(final String[] firstLine) {
        if (firstLine.length != 3) {
            throw new IllegalArgumentException(String.format("Wrong Http Request Start Line : %s", String.join("", firstLine)));
        }
    }

    private static Map<String, String> getHeaders(final String[] headerLines) {
        String line;
        final Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < headerLines.length; i++) {
            line = headerLines[i];

            final String[] headerLine = line.split(HEADER_DELIMITER);
            if (headerLine.length != 2) {
                throw new IllegalArgumentException(String.format("Wrong Http Request Header : %s", line));
            }

            headers.put(headerLine[0], headerLine[1]);
        }
        return headers;
    }

    public Map<String, String> getBodyByContentType(final HttpContentType contentType) {
        if (contentType != HttpContentType.URL) {
            throw new IllegalArgumentException("Request Content-Type should be application/x-www-form-urlencoded");
        }

        if (body == null || body.isEmpty()) {
            return new HashMap<>();
        }

        final Map<String, String> urlEncodedResponseBody = parseUrlEncoded(body);
        return urlEncodedResponseBody;
    }

    public Optional<String> findCookie(final String cookieName) {
        if (cookie == null) {
            return Optional.empty();
        }
        return cookie.findCookie(cookieName);
    }

    private Http11Request(
            final String method,
            final String path,
            final Map<String, String> queryParams,
            final String protocolVersion,
            final Map<String, String> headers,
            final Http11Cookie cookie,
            final String body
    ) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.cookie = cookie;
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }
}
