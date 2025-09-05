package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11Request {
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

        final Map<String, String> queryParams = new HashMap<>();
        if (target.contains("?")) {
            final String[] targetWithQueryParams = target.split("[=?&]");
            int queryParamPointer = 1;
            while (queryParamPointer < targetWithQueryParams.length) {
                final String key = targetWithQueryParams[queryParamPointer++];
                final String value = targetWithQueryParams[queryParamPointer++];

                queryParams.put(key, value);
            }
        }

        String line;
        final Map<String, String> headers = new HashMap<>();
        while (pointer < requestMessage.size()) {
            line = requestMessage.get(pointer++);
            if (line.isBlank()) {
                break;
            }

            final String[] headerLine = line.split(": ");

            if (headerLine.length != 2) {
                throw new IllegalArgumentException("Wrong Http Request Header");
            }

            headers.put(headerLine[0], headerLine[1]);
        }

        return new Http11Request(method, target, queryParams, httpVersion, headers, null);

//        final StringBuilder body = new StringBuilder();
//        while ((line = requestMessage.get(pointer++)).isBlank()) {
//            body.append(line);
//        }
//
//        if (body.isEmpty()) {
//        }
//        return new Http11Request(method, target, queryParams, httpVersion, headers, body.toString());
    }

    private Http11Request(final String method, final String target, final Map<String, String> queryParams, final String httpVersion, final Map<String, String> headers, final String body) {
        this.method = method;
        this.target = target;
        this.queryParams = queryParams;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public String getTarget() {
        return target;
    }

    public String findQueryParam(String key) {
        if (queryParams.containsKey(key)) {
            return queryParams.get(key);
        }
        throw new IllegalArgumentException("No Query Parameter : " + key);
    }
}
