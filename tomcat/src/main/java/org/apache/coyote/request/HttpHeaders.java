package org.apache.coyote.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String KEY_VALUE_SEPARATOR = ":";

    private final Map<String, String> value;

    private HttpHeaders(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpHeaders from(final List<String> requests) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < requests.size(); i++) {
            String target = requests.get(i).strip();
            int index = target.indexOf(KEY_VALUE_SEPARATOR);
            headers.put(target.substring(0, index), target.substring(index + 1));
        }
        return new HttpHeaders(headers);
    }

    public Map<String, String> getValue() {
        return value;
    }
}
