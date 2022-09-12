package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.util.StringUtils;

public class QueryParams {

    private static final String QUERY_DELIMITER = "?";
    private Map<String, String> queryParams = new HashMap<>();

    public QueryParams(String url) {
        if (url.contains(QUERY_DELIMITER)) {
            String parameters = url.split("\\" + QUERY_DELIMITER)[1];
            queryParams = StringUtils.parseKeyAndValues(parameters);
        }
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
