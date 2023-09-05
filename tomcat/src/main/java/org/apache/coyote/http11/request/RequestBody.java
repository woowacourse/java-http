package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private final Map<String, String> bodyContents = new HashMap<>();

    public RequestBody(Map<String, String> body) {
        bodyContents.putAll(body);
    }

    public static RequestBody from(String requestBody) {
        final Map<String, String> bodyContents = new HashMap<>();
        String[] bodyElements = requestBody.split("&");
        List<String[]> bodyKeyValues = Arrays.stream(bodyElements)
                .map(s -> s.split("="))
                .collect(Collectors.toList());

        for (String[] keyValue : bodyKeyValues) {
            bodyContents.put(keyValue[0], keyValue[1]);
        }

        return new RequestBody(bodyContents);
    }

    public String getByKey(String key) {
        return bodyContents.getOrDefault(key, null);
    }
}
