package nextstep.jwp.http.http_request;

import nextstep.jwp.exception.NotFoundParamException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JwpPath {

    private static final String QUERY_STRING_SYMBOL = "?";
    private static final String PARAM_DELIMITER = "&";
    private static final String PARAM_KEY_AND_VALUE_DELIMITER = "=";

    private final String uri;
    private final Map<String, String> queryParams;

    public JwpPath(String uri, Map<String, String> queryParams) {
        this.uri = uri;
        this.queryParams = queryParams;
    }

    public static JwpPath of(String requestUri) {
        if (!requestUri.contains(QUERY_STRING_SYMBOL)) {
            return new JwpPath(requestUri, Collections.emptyMap());
        }

        int index = requestUri.indexOf(QUERY_STRING_SYMBOL);
        String path = requestUri.substring(0, index);
        String queryString = requestUri.substring(index + 1);
        String[] queryParams = queryString.split(PARAM_DELIMITER);
        Map<String, String> params = parseParams(queryParams);
        return new JwpPath(path, params);
    }

    private static Map<String, String> parseParams(String[] queryParams) {
        Map<String, String> params = new HashMap<>();
        for (String queryParam : queryParams) {
            splitKeyAndValue(params, queryParam, PARAM_KEY_AND_VALUE_DELIMITER);
        }
        return params;
    }

    private static void splitKeyAndValue(Map<String, String> params, String queryParam, String delimiter) {
        String[] keyAndValue = queryParam.split(delimiter);
        params.put(keyAndValue[0].trim(), keyAndValue[1].trim());
    }

    public boolean hasQueryStrings() {
        return !queryParams.isEmpty();
    }

    public String getUri() {
        return uri;
    }

    public String getQueryParam(String key) {
        if (!queryParams.containsKey(key)) {
            throw new NotFoundParamException(key);
        }

        return queryParams.get(key);
    }
}
