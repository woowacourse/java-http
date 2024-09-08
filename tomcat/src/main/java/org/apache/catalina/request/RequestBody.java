package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String BODY_DELIMITER = "&";
    public static final String SPLIT_DELIMITER = "=";

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody parse(String rawBody) {
        Map<String, String> body = new HashMap<>();

        String[] params = rawBody.split(BODY_DELIMITER);
        for (String param : params) {
            String[] keyValue = param.split(SPLIT_DELIMITER);
            body.put(keyValue[0], keyValue[1]);
        }

        return new RequestBody(body);
    }

    public static RequestBody empty() {
        return new RequestBody(Map.of());
    }

    public Map<String, String> getBody() {
        return body;
    }
}
