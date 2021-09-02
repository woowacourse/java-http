package nextstep.jwp.framework.http.request.details;

import nextstep.jwp.framework.http.request.util.QueryParameterExtractor;

import java.util.Map;

import static nextstep.jwp.framework.http.common.Constants.BLANK;

public class QueryParameter {

    private final Map<String, String> queryParams;

    private QueryParameter(final Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParameter of(final String queryString) {
        final Map<String, String> requestQueryString = QueryParameterExtractor.extract(queryString);
        return new QueryParameter(requestQueryString);
    }

    public String searchValue(final String key) {
        if (queryParams.containsKey(key)) {
            return queryParams.get(key);
        }
        return BLANK;
    }
}
