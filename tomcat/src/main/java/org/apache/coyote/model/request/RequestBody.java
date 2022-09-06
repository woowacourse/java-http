package org.apache.coyote.model.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    public static final int KEY = 0;
    public static final int VALUE = 1;
    private final Map<String, String> requestBody;

    private RequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody of(final String input) {
        if (input.equals("")) {
            return new RequestBody(Collections.EMPTY_MAP);
        }
        return createBody(input);
    }

    private static RequestBody createBody(String input) {
        Map<String, String> requestBody = new HashMap<>();
        String[] body = input.split("&");
        for (String value : body) {
            String[] contents = value.split("=");
            requestBody.put(contents[KEY], contents[VALUE]);
        }
        return new RequestBody(requestBody);
    }

    public String getByKey(String key) {
        return requestBody.get(key);
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }
}
