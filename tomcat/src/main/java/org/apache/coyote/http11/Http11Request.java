package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Http11Request {

    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE_HEADER = "Cookie";

    private static final AtomicInteger pointer = new AtomicInteger(0);

    private final String method;
    private final String target;
    private final Map<String, String> queryParams;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Http11Cookie cookie;
    private final String body;

    public static Http11Request create(final List<String> requestMessage) {
        pointer.set(0);
        final String[] firstLine = requestMessage.get(pointer.getAndIncrement()).split(" ");
        validateFirstLineSize(firstLine);

        final String method = firstLine[0];
        String target = firstLine[1];
        final String httpVersion = firstLine[2];

        final Map<String, String> queryParams = getQueryParams(target);
        if (!queryParams.isEmpty()) {
            final int queryParamStartIndex = target.indexOf("?");
            target = target.substring(0, queryParamStartIndex);
        }

        final Map<String, String> headers = getHeaders(requestMessage);
        Http11Cookie cookie = null;
        if (headers.containsKey(COOKIE_HEADER)) {
            cookie = Http11Cookie.create(headers.get(COOKIE_HEADER));
            headers.remove(COOKIE_HEADER);
        }

        final String body = getBody(requestMessage);

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
            final List<String> requestMessage
    ) {
        String line;
        final Map<String, String> headers = new HashMap<>();
        while (pointer.get() < requestMessage.size()) {
            line = requestMessage.get(pointer.getAndIncrement());
            if (line.isBlank()) {
                break;
            }

            final String[] headerLine = line.split(HEADER_DELIMITER);
            if (headerLine.length != 2) {
                throw new IllegalArgumentException(String.format("Wrong Http Request Header : %s", line));
            }

            headers.put(headerLine[0], headerLine[1]);
        }
        return headers;
    }

    private static String getBody(
            final List<String> requestMessage
    ) {
        String line;
        final StringBuilder sb = new StringBuilder();
        while (pointer.get() < requestMessage.size()) {
            line = requestMessage.get(pointer.getAndIncrement());

            sb.append(line);
        }
        return sb.toString();
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

    public Optional<String> findQueryParam(final String key) {
        if (queryParams.containsKey(key)) {
            return Optional.of(queryParams.get(key));
        }
        return Optional.empty();
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
