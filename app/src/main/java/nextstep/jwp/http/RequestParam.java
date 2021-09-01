package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.entity.HttpBody;

public class RequestParam {
    private final Map<String, String> params;

    private RequestParam(Map<String, String> params) {
        this.params = params;
    }

    public static RequestParam of(String requestBody) {
        String[] strings = requestBody.split("&");

        Map<String, String> queryMap = new HashMap<>();

        for (String string : strings) {
            String[] token = string.split("=");
            queryMap.put(token[0], token[1]);
        }
        return new RequestParam(queryMap);
    }

    public static RequestParam of(HttpBody requestBody) {
        return of(requestBody.body());
    }

    public String get(String key) {
        return params.get(key);
    }
}
