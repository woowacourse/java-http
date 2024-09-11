package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import org.apache.coyote.util.QueryParameterParser;

public class HttpRequestTarget {

    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final String QUERY_PARAMETER_DELIMITER_REGEX = "\\?";
    private static final int PATH_INDEX = 0;
    private static final int QUERY_PARAMETER_INDEX = 1;

    private final String path;
    private final QueryParameters queryParameters;

    private HttpRequestTarget(String path, QueryParameters queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static HttpRequestTarget from(String target) {
        if (target.contains(QUERY_PARAMETER_DELIMITER)) {
            String[] values = target.split(QUERY_PARAMETER_DELIMITER_REGEX);
            Map<String, List<String>> parameters = QueryParameterParser.parse(values[QUERY_PARAMETER_INDEX]);
            return new HttpRequestTarget(values[PATH_INDEX], new QueryParameters(parameters));
        }
        return new HttpRequestTarget(target, new QueryParameters(Map.of()));
    }

    public String getPath() {
        return path;
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }

    @Override
    public String toString() {
        if (queryParameters.hasParameters()) {
            return String.format("%s?%s", path, queryParameters);
        }
        return path;
    }
}
