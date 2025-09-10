package org.apache.coyote.http11.http.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final byte[] value;

    private HttpRequestBody(final byte[] value) {
        this.value = value;
    }

    public static HttpRequestBody from(final byte[] bytes) {
        return new HttpRequestBody(bytes);
    }

    public Map<String, String> getBodyElement() {
        String bodyLine = new String(value, StandardCharsets.UTF_8);
        return parseBodyValue(bodyLine);
    }

    private Map<String, String> parseBodyValue(final String target) {
        final Map<String, String> bodyValue = new HashMap<>();
        final String[] elements = target.split("&");

        for (String element : elements) {
            final String[] values = element.split("=");
            final String key = URLDecoder.decode(values[0], StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(values[1], StandardCharsets.UTF_8);
            bodyValue.put(key, value);
        }

        return bodyValue;
    }

    public byte[] getValue() {
        return value;
    }
}
