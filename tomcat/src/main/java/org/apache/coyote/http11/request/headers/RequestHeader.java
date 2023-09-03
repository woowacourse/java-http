package org.apache.coyote.http11.request.headers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.auth.Cookie;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;

public class RequestHeader {
    private static final String COOKIE_HEADER = "Cookie";

    private final Map<String, List<String>> headers;

    private RequestHeader(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(List<String> headers) {
        return new RequestHeader(convertKeyAndValue(headers));
    }

    private static Map<String, List<String>> convertKeyAndValue(List<String> headers) {
        Map<String, List<String>> keyAndValues = new HashMap<>();
        for (String header : headers) {
            List<String> keyAndValue = asList(header.split(":"));
            String key = keyAndValue.get(0);
            List<String> values = removalSpace(asList(keyAndValue.get(1).split(",")));
            keyAndValues.put(key, values);
        }
        return keyAndValues;
    }

    private static List<String> removalSpace(List<String> values) {
        return values.stream()
                .map(value -> value.trim())
                .collect(Collectors.toList());
    }

    public List<String> get(String key) {
        return headers.get(key);
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public Cookie getCookie() {
        final List<String> cookie = headers.getOrDefault(COOKIE_HEADER, EMPTY_LIST);
        return Cookie.from(cookie);
    }

}
