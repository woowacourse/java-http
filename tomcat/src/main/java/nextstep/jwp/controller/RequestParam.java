package nextstep.jwp.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestParam {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_STRING_DELIMITER = "&";

    private final Map<String, String> params;

    private RequestParam(final Map<String, String> params) {
        this.params = params;
    }

    public static RequestParam of(String queryString) {
        Map<String, String> params = new HashMap<>();
        String decodeQueryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
        String[] pairs = decodeQueryString.split(QUERY_STRING_DELIMITER);
        for (String pair : pairs) {
            String[] query = pair.split(KEY_VALUE_DELIMITER);
            params.put(
                    query[KEY_INDEX],
                    query[VALUE_INDEX]
            );
        }
        return new RequestParam(params);
    }

    public String get(String key) {
        return params.get(key);
    }
}
