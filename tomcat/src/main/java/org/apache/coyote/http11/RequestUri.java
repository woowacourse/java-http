package org.apache.coyote.http11;

import org.apache.coyote.exception.HttpParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestUri {
    private static final String URI_PREFIX = "/";
    private static final String QUERY_DELIMITER = "\\?";
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final String path;
    private final Map<String, String> queryParameters;

    public RequestUri(String uri) {
        validateUri(uri);
        String[] uriParts = uri.split(QUERY_DELIMITER, 2);
        this.path = uriParts[0];
        if (uriParts.length == 1) {
            this.queryParameters = Map.of();
            return;
        }
        this.queryParameters = addQueryParameters(uriParts[1]);
    }

    private void validateUri(String uri) {
        if (!uri.startsWith(URI_PREFIX)) {
            throw new HttpParseException("요청 경로는 '/'로 시작해야 합니다: " + uri);
        }
    }

    private Map<String, String> addQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();

        String[] parameters = queryString.split(QUERY_PARAM_DELIMITER);
        for (String parameter : parameters) {
            if (parameter.isBlank()) {
                continue;
            }
            String[] keyValue = parameter.split(KEY_VALUE_DELIMITER, 2);
            if (keyValue.length != 2) {
                continue;
            }
            queryParameters.put(keyValue[0], keyValue[1]);
        }
        return queryParameters;
    }

    public String getPath() {
        return path;
    }

    public Optional<String> findParameter(String key) {
        return Optional.ofNullable(queryParameters.get(key));
    }
}
