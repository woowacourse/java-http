package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Http11Request {

    private static final String HEADER_DELIMITER = ": ";

    private static int pointer;

    private final String method;
    private final String target;
    private final Map<String, String> queryParams;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final String body;

    public static Http11Request create(final List<String> requestMessage) {
        pointer = 0;
        final String[] firstLine = requestMessage.get(pointer++).split(" ");
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

        final String body = getBody(requestMessage);

        return new Http11Request(method, target, queryParams, httpVersion, headers, body);
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
        while (pointer < requestMessage.size()) {
            line = requestMessage.get(pointer++);
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
        while (pointer < requestMessage.size()) {
            line = requestMessage.get(pointer++);

            sb.append(line + "\n");
        }
        return sb.toString();
    }

    private Http11Request(
            final String method,
            final String target,
            final Map<String, String> queryParams,
            final String httpVersion,
            final Map<String, String> headers,
            final String body
    ) {
        this.method = method;
        this.target = target;
        this.queryParams = queryParams;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public Optional<String> findQueryParam(final String key) {
        if (queryParams.containsKey(key)) {
            return Optional.of(queryParams.get(key));
        }
        return Optional.empty();
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
