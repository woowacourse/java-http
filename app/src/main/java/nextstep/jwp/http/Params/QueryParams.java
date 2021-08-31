package nextstep.jwp.http.Params;

import java.util.Map;

public class QueryParams {

    private final Map<String, String> queryParamData;

    public QueryParams(final Map<String, String> queryParamData) {
        this.queryParamData = queryParamData;
    }

    public void put(final String key, final String value) {
        queryParamData.put(key, value);
    }

    public String get(final String key) {
        return queryParamData.get(key);
    }

    public boolean isEmpty() {
        return queryParamData.isEmpty();
    }
}
