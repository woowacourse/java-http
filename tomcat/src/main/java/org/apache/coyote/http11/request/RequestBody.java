package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    private final Map<String, String> body;

    public RequestBody(String body) {
        this.body = parseRequestBody(body);
    }

    private Map<String, String> parseRequestBody(String body) {
        Map<String, String> bodyParams = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], Charset.defaultCharset());
                String value = URLDecoder.decode(keyValue[1], Charset.defaultCharset());
                bodyParams.put(key, value);
            }
        }
        return bodyParams;
    }

    public Map<String, String> getBody() {
        return Map.copyOf(body);
    }

    public String getAttribute(String attribute) {
        return body.get(attribute);
    }
}
