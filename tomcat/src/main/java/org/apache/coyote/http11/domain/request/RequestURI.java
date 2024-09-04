package org.apache.coyote.http11.domain.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestURI {

    private static final String QUESTION_MARK = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final String path;
    private final Map<String, String> queryParameters;

    public RequestURI(String requestURI) {
        this.queryParameters = new HashMap<>();

        int queryIndex = requestURI.indexOf(QUESTION_MARK);
        if (isQueryEmpty(queryIndex)) {
            this.path = requestURI;
            return;
        }

        this.path = requestURI.substring(0, queryIndex);
        String query = requestURI.substring(queryIndex + 1);
        parseQuery(query);
    }

    private void parseQuery(String query) {
        Arrays.stream(query.split(PARAMETER_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .forEach(param -> queryParameters.put(param[0], param[1]));
    }

    private boolean isQueryEmpty(int queryIndex) {
        return queryIndex == -1;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getQueryParameter(String key) {
        return queryParameters.get(key);
    }
}
