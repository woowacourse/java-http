package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11Request {

    private static final String HEADER_DELIMITER = ": ";
    private static final String QUERY_PARAM_START_SYMBOL = "?";
    private static final String QUERY_PARAM_DELIMITER = "[=?&]";

    private final String method;
    private final String target;
    private final Map<String, String> queryParams;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final String body;

    public static Http11Request create(final List<String> requestMessage) {
        int pointer = 0;

        final String[] firstLine = requestMessage.get(pointer++).split(" ");
        final String method = firstLine[0];
        final String target = firstLine[1];
        final String httpVersion = firstLine[2];

        final Map<String, String> queryParams = getQueryParams(target);
        final Map<String, String> headers = getHeaders(requestMessage, pointer);

        return new Http11Request(method, target, queryParams, httpVersion, headers, null);
    }

    private static Map<String, String> getQueryParams(final String target) {
        final Map<String, String> queryParams = new HashMap<>();
        if (target.contains(QUERY_PARAM_START_SYMBOL)) {
            final String[] targetWithQueryParams = target.split(QUERY_PARAM_DELIMITER);
            int queryParamPointer = 1;
            while (queryParamPointer < targetWithQueryParams.length) {
                final String key = targetWithQueryParams[queryParamPointer++];
                final String value = targetWithQueryParams[queryParamPointer++];

                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    private static Map<String, String> getHeaders(
            final List<String> requestMessage,
            int pointer
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
                throw new IllegalArgumentException("Wrong Http Request Header");
            }

            headers.put(headerLine[0], headerLine[1]);
        }
        return headers;
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

    public String findQueryParam(final String key) {
        if (queryParams.containsKey(key)) {
            return queryParams.get(key);
        }
        throw new IllegalArgumentException("No Query Parameter : " + key);
    }

    public String getTarget() {
        return target;
    }
}
