package org.apache.coyote.http.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHeader {

    private final Map<String, String> values;

    public RequestHeader(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestHeader from(final List<String> requestHeaderStrings) {
        return new RequestHeader(convertToRequestHeaderMap(requestHeaderStrings));
    }

    private static Map<String, String> convertToRequestHeaderMap(List<String> requestHeaderStrings) {
        Map<String, String> requestHeaderMap = new ConcurrentHashMap<>();

        if (requestHeaderStrings.isEmpty()) {
            return requestHeaderMap;
        }

        for (String requestHeaderString : requestHeaderStrings) {
            String[] keyValuePair = requestHeaderString.split(": ");
            validateKeyValuePair(keyValuePair);
            requestHeaderMap.put(keyValuePair[0], keyValuePair[1]);
        }

        return requestHeaderMap;
    }

    private static void validateKeyValuePair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            throw new UncheckedServletException("request header의 형식은 'key: value' 이여야 합니다.");
        }
    }

    public boolean hasContentLengthKey() {
        return values.containsKey("Content-Length");
    }

    public boolean hasCookieKey() {
        return values.containsKey("Cookie");
    }

    public int getContentLength() {
        if (!hasContentLengthKey()) {
            throw new UncheckedServletException("content가 없습니다.");
        }
        return Integer.parseInt(values.get("Content-Length"));
    }

    public String getCookie() {
        if (!hasCookieKey()) {
            return "";
        }
        return values.get("Cookie");
    }
}
