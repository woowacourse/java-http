package nextstep.jwp.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParams {

    public static Map<String, String> requestParams(String[] requestBody) {
        final Map<String, String> params = new LinkedHashMap<>();
        for (String b : requestBody) {
            final String[] split = b.split("=");
            params.put(split[0].trim(), split[1].trim());
        }
        return params;
    }
}
