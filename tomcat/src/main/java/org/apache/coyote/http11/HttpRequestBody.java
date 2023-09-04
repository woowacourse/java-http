package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestBody {
    private final Map<String, String> parameters;

    private HttpRequestBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(Map.of());
    }

    public static HttpRequestBody of(final ContentType contentType, final String body) {
        if (contentType == ContentType.FORM_URLENCODED) {
            final HashMap<String, String> parameters = new HashMap<>();
            final String[] tokens = body.split("&");
            for (String token : tokens) {
                final List<String> keyValues = List.of(token.split("="));
                if (keyValues.size() == 2) {
                    parameters.put(keyValues.get(0), keyValues.get(1));
                } else {
                    parameters.put(keyValues.get(0), "");
                }
            }
            return new HttpRequestBody(parameters);
        }
        throw new IllegalArgumentException("지원하지 않는 Content-Type입니다.");
    }

    public String get(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
    }

    @Override
    public String toString() {
        return "HttpRequestBody{" +
                "parameters=" + parameters +
                '}';
    }
}
