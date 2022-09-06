package org.apache.coyote.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final String QUERY_DELIMETER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String INDEX_DELIMITER = "?";

    private final HttpMethod httpMethod;
    private final String url;
    private final Map<String, String> query;
    private final Header header;
    private final String body;


    public HttpRequest(final String requestLine, Header header, String body) {
        final String[] parseRequest = requestLine.split(" ");
        this.httpMethod = HttpMethod.from(parseRequest[0]);
        final Integer index = parseRequest[1].indexOf(INDEX_DELIMITER);
        this.url = parseUrl(parseRequest[1], index);
        this.query = parseQuery(parseRequest[1], index);
        this.header = header;
        this.body = body;
    }

    private String parseUrl(final String uri, final Integer index) {
        if (hasQuery(index)) {
            return uri.substring(0, index);
        }
        return uri;
    }

    private Map<String, String> parseQuery(final String uri, final Integer index) {
        if (hasQuery(index)) {
            String queryString = uri.substring(index + 1);
            final String[] s = queryString.split(QUERY_DELIMETER);
            return Arrays.stream(s)
                    .map(q -> q.split(KEY_VALUE_DELIMITER))
                    .collect(Collectors.toMap(key -> key[0], value -> value[1]));
        }
        return new HashMap<>();
    }

    private boolean hasQuery(final int index) {
        return index >= 0;
    }

    public boolean hasQuery() {
        return query.size() != 0;
    }

    public String getQueryByValue(final String key) {
        final String value = query.get(key);
        validateNull(key, value);
        return value;
    }

    private void validateNull(String key, String value) {
        if (value == null) {
            throw new NoSuchElementException(String.format("%s의 값을 찾을 수 없습니다.", key));
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQuery() {
        return query;
    }
}
