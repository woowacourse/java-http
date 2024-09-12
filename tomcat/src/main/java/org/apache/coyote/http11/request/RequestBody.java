package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private Map<String, String> parameters;

    public static RequestBody create(String body) {
        Map<String, String> parameters = new HashMap<>();
        Arrays.stream(body.split("&"))
                .forEach(requestBodyField -> {
                    String[] split = requestBodyField.split("=");
                    if (split.length == 2 && !split[0].isBlank() && !split[1].isBlank()) {
                        parameters.put(split[0], split[1].trim());
                    }
                });

        return new RequestBody(parameters);
    }

    private RequestBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
