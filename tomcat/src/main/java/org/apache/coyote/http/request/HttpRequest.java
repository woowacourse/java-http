package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http.cookie.HttpCookie;

public class HttpRequest {

    private final String method;
    private final String endpoint;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String endpoint, Map<String, String> headers, String body) {
        this.method = method;
        this.endpoint = endpoint;
        this.headers = Map.copyOf(headers);
        this.body = body;
    }

    public Map<String, String> parseFormData() {
        if (!hasBody()) {
            return Map.of();
        }

        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return Map.copyOf(params);
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getCookies() {
        return HttpCookie.parse(headers.get("Cookie"));
    }

    public boolean hasBody() {
        return !body.isEmpty();
    }
}
