package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;

public record RequestBody(
        Map<String, Object> requestValues
) {

    public static RequestBody createByFormData(String formData) {
        Map<String, Object> requestValues = new HashMap<>();
        String[] requestValuePairs = formData.split("&");
        for (String requestPair : requestValuePairs) {
            String[] keyValue = requestPair.split("=");
            requestValues.put(keyValue[0], keyValue[1]);
        }
        return new RequestBody(requestValues);
    }

    public static RequestBody createEmpty() {
        return new RequestBody(new HashMap<>());
    }

    public String getValue(final String requestKey) {
        return (String)requestValues.get(requestKey);
    }
}
