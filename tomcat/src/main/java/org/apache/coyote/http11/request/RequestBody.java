package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> values;

    public RequestBody() {
        this(new HashMap<>());
    }

    private RequestBody(Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody from(String body) {
        if (body.isBlank()) {
            return new RequestBody();
        }
        HashMap<String, String> values = new HashMap<>();
        String[] keyAndValue = body.split("&");
        for (String element : keyAndValue) {
            String[] split = element.split("=");
            values.put(split[0], split[1]);
        }
        return new RequestBody(values);
    }

    public static RequestBody empty() {
        return new RequestBody();
    }

    public String getValueOf(String key) {
        return values.get(key);
    }


    public boolean isEmpty() {
        return values.isEmpty();
    }

}
