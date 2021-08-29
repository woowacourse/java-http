package nextstep.jwp.request;

import java.util.Map;
import java.util.Objects;

public class QueryParameter {

    private final Map<String, String> queryParams;

    public QueryParameter(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParameter of(String queryString) {
        final Map<String, String> requestQueryString = QueryParameterExtractor.extract(queryString);
        return new QueryParameter(requestQueryString);
    }

    public String searchValue(String key) {
        if (queryParams.containsKey(key)) {
            return queryParams.get(key);
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryParameter that = (QueryParameter) o;
        return Objects.equals(queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryParams);
    }
}
