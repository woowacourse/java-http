package org.apache.coyote.http11.request.body;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Params {

    private final Map<String, String> params;

    private Params(final Map<String, String> params) {
        this.params = params;
    }

    public static Params from(final String queryString) {
        String decodedQueryString = URLDecoder.decode(queryString);
        Map<String, String> params = Arrays.stream(decodedQueryString.split("&"))
                .map(param -> param.split("=", 2))
                .filter(param -> param.length == 2)
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]
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
