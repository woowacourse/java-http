package nextstep.jwp.controller;

import java.io.UnsupportedEncodingException;
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

    public static RequestParam of(String queryString) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split(QUERY_STRING_DELIMITER);
        for (String pair : pairs) {
            String[] query = pair.split(KEY_VALUE_DELIMITER);
            params.put(
                    URLDecoder.decode(query[KEY_INDEX], StandardCharsets.UTF_8),
                    URLDecoder.decode(query[VALUE_INDEX], StandardCharsets.UTF_8)
            );
        }
        return new RequestParam(params);
    }

    public String get(String key) {
        return params.get(key);
    }
}
