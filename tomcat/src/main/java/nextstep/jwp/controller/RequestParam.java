package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

public class RequestParam {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> params;

    private RequestParam(final Map<String, String> params) {
        this.params = params;
    }

    public static RequestParam of(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] partQuery = queryString.split("&");
        for (String query : partQuery) {
            String[] split = query.split("=");
            params.put(split[KEY_INDEX], split[VALUE_INDEX]);
        }
        return new RequestParam(params);
    }

    public String get(String key) {
        return params.get(key);
    }
}
