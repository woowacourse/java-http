package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Http11Request {

    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE_HEADER = "Cookie";

    private final String method;
    private final String target;
    private final Map<String, String> queryParams;
    private final String httpVersion;
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
        String target = firstLine[1];
        final String httpVersion = firstLine[2];

        final Map<String, String> queryParams = getQueryParams(target);
        if (!queryParams.isEmpty()) {
            final int queryParamStartIndex = target.indexOf("?");
            target = target.substring(0, queryParamStartIndex);
        }

        final Map<String, String> headers = getHeaders(headerLines);
        Http11Cookie cookie = null;
        if (headers.containsKey(COOKIE_HEADER)) {
            cookie = Http11Cookie.create(headers.get(COOKIE_HEADER));
            headers.remove(COOKIE_HEADER);
        }

        return new Http11Request(method, target, queryParams, httpVersion, headers, cookie, body);
    }

    private static void validateFirstLineSize(final String[] firstLine) {
        if (firstLine.length != 3) {
            throw new IllegalArgumentException(String.format("Wrong Http Request Start Line : %s", String.join("", firstLine)));
        }
    }

    private static Map<String, String> getQueryParams(final String target) {
        final Map<String, String> params = new HashMap<>();
        if (target == null || target.isEmpty()) {
            return params;
        }

        final String[] targetAndQueryParams = target.split("\\?");
        if (targetAndQueryParams.length != 2) {
            return params;
        }

        final String queryParams = targetAndQueryParams[1];

        final String[] pairs = queryParams.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
                continue;
            }
            throw new IllegalArgumentException(String.format("Wrong Query Parameter : %s", pair));
        }

        return params;
    }

    private static Map<String, String> getHeaders(
            final String[] headerLines
    ) {
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

    public Http11Request(
            final String method,
            final String target,
            final Map<String, String> queryParams,
            final String httpVersion,
            final Map<String, String> headers,
            final Http11Cookie cookie,
            final String body
    ) {
        this.method = method;
        this.target = target;
        this.queryParams = queryParams;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.cookie = cookie;
        this.body = body;
    }

    public Map<String, String> getBodyByContentType(final String contentType) {
        if (!contentType.equals("application/x-www-form-urlencoded")) {
            throw new IllegalArgumentException("Request Content-Type should be application/x-www-form-urlencoded");
        }

        final Map<String, String> urlEncodedResponseBody = new HashMap<>();

        if (body == null || body.isEmpty()) {
            return urlEncodedResponseBody;
        }

        final String[] pairs = body.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                urlEncodedResponseBody.put(keyValue[0], keyValue[1]);
                continue;
            }
            throw new IllegalArgumentException(String.format("Wrong x-www-form-urlencoded request : %s", pair));
        }
        return urlEncodedResponseBody;
    }

    public Optional<String> findCookie(final String cookieName) {
        if (cookie == null) {
            return Optional.empty();
        }
        return cookie.findCookie(cookieName);
    }

    public String getTarget() {
        return target;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;

    }
}
