package nextstep.jwp.http;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";

    private static final int QUERY_PARAM_KEY_INDEX = 0;
    private static final int QUERY_PARAM_VALUE_INDEX = 1;

    private final Map<String, String> values;

    public RequestParams(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestParams from(final String queryParameter) {
        return new RequestParams(convertQueryParmeterMap(queryParameter));
    }

    private static Map<String, String> convertQueryParmeterMap(final String queryParameter) {
        Map<String, String> queryParams = new ConcurrentHashMap<>();
        if (queryParameter.isEmpty()) {
            return queryParams;
        }
        String[] queryParamUris = queryParameter.split(QUERY_PARAM_DELIMITER);
        for (String queryParamUri : queryParamUris) {
            String[] param = queryParamUri.split(QUERY_PARAM_VALUE_DELIMITER);
            queryParams.put(param[QUERY_PARAM_KEY_INDEX], param[QUERY_PARAM_VALUE_INDEX]);
        }
        return queryParams;
    }

    public Map<String, String> getValues() {
        return values;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RequestParams that = (RequestParams) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
