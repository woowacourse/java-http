package nextstep.jwp.controller;

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

    //FIXME: URLDecoder.decode URL 인코딩된 문자열을 그대로 저장하면, 이후 문제가 발생할 가능성이 있습니다.
    public static RequestParam of(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split(QUERY_STRING_DELIMITER);
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
