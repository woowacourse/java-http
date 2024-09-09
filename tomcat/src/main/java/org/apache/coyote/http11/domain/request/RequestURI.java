package org.apache.coyote.http11.domain.request;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RequestURI {

    private static final String QUERY_DELIMITER = "?";

    private final String path;
    private final QueryParameters queryParameters;

    public RequestURI(String requestURI) {
        int questionMarkIndex = requestURI.indexOf(QUERY_DELIMITER);
        if (isQueryEmpty(questionMarkIndex)) {
            this.path = requestURI;
            this.queryParameters = new QueryParameters(StringUtils.EMPTY);
            return;
        }

        this.path = requestURI.substring(0, questionMarkIndex);
        this.queryParameters = new QueryParameters(requestURI.substring(questionMarkIndex + 1));
    }

    private boolean isQueryEmpty(int queryIndex) {
        return queryIndex == -1;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters.getParameters();
    }

    public String getQueryParameter(String key) {
        return queryParameters.get(key);
    }
}
