package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private final Map<String, String> body;

    private RequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(String requestBody) {
        Map<String, String> body = Arrays.stream(requestBody.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

        return new RequestBody(body);
    }

    public static RequestBody empty() {
        return new RequestBody(Map.of());
    }

    public Map<String, String> getParams() {
        return body;
    }

    @Override
    public String toString() {
        return body.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }
}
