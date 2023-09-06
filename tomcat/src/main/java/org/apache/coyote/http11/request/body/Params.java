package org.apache.coyote.http11.request.body;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.Constant.EQUALS_VALUE;
import static org.apache.coyote.http11.Constant.PARAM_SEPARATOR;

public class Params {

    private static final int KEY = 0;
    private static final int INDEX = 1;
    private static final int SPLIT_LIMIT = 2;
    private static final int PARAM_LENGTH = 2;

    private final Map<String, String> params;

    private Params(final Map<String, String> params) {
        this.params = params;
    }

    public static Params from(final String queryString) {
        String decodedQueryString = URLDecoder.decode(queryString);

        Map<String, String> params = Arrays.stream(decodedQueryString.split(PARAM_SEPARATOR))
                .map(param -> param.split(EQUALS_VALUE, SPLIT_LIMIT))
                .filter(param -> param.length == PARAM_LENGTH)
                .collect(Collectors.toMap(
                        param -> param[KEY],
                        param -> param[INDEX]
                ));

        return new Params(params);
    }

    public static Params createEmpty() {
        return new Params(Map.of());
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public boolean hasParam(String param) {
        return params.containsKey(param);
    }

    public String get(String key) {
        return params.get(key);
    }

    public Map<String, String> getParams() {
        return params;
    }
}
