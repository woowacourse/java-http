package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private final Map<String, String> params;

    public QueryParams(Map<String, String> params) {
        this.params = params;
    }

    public QueryParams(String paramString) {
        this(parseParams(paramString));
    }

    public QueryParams() {
        this(new HashMap<>());
    }

    private static Map<String, String> parseParams(String paramString) {
        Map<String, String> params = new HashMap<>();
        for (String queryString : paramString.split("&")) {
            String[] split = queryString.split("=");
            params.put(split[0], split[1]);
        }
        return params;
    }

    public String get(String param) {
        return params.get(param);
    }
}
