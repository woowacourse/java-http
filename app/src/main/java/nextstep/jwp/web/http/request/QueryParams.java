package nextstep.jwp.web.http.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryParams {

    private final Map<String, String> params;

    public QueryParams() {
        this(new LinkedHashMap<>());
    }

    public QueryParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> map(){
        return Map.copyOf(params);
    }

    public String get(String key) {
        return params.get(key);
    }
}
