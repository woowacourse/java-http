package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestBody {

    private final Map<String, String> body;

    public HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static HttpRequestBody from(String requestBody) {
        Map<String, String> body = Arrays.stream(requestBody.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        return new HttpRequestBody(body);
    }

    public String getValue(String name) {
        return body.get(name);
    }
}
