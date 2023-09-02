package org.apache.coyote.http11.message.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestURI {

    private final String value;

    private RequestURI(String value) {
        this.value = value;
    }

    public static RequestURI from(String value) {
        return new RequestURI(value);
    }

    public String absolutePath() {
        if (hasQueryParameters()) {
            return value.substring(0, value.indexOf("?"));
        }
        return value;
    }

    public Map<String, String> queryParameters() {
        String[] split = value.substring(value.indexOf("?") + 1)
                .split("&");

        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(split)
                .map(each -> each.split("="))
                .forEach(each -> queryParameters.put(each[0], each[1]));

        return Collections.unmodifiableMap(queryParameters);
    }

    public boolean hasQueryParameters() {
        return value.contains("?");
    }
}
