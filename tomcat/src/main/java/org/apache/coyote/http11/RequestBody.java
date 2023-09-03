package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody emptyBody() {
        return new RequestBody(Collections.unmodifiableMap(new HashMap<>()));
    }

    public static RequestBody from(final String requestBody) {
        final Map<String, String> body = new HashMap<>();
        final String[] keyValues = requestBody.split("&");
        for (String keyValue : keyValues) {
            final String[] splitKeyValue = keyValue.split("=");
            body.put(splitKeyValue[0], splitKeyValue[1]);
        }
        return new RequestBody(body);
    }

    public Map<String, String> getBody() {
        return body;
    }

    public String get(final String key) {
        return body.get(key);
    }
}
