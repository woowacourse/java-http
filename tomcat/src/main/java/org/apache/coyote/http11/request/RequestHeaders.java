package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.HttpCookie;

public class RequestHeaders {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    private RequestHeaders(Map<String, String> value) {
        this.value = value;
    }

    public static RequestHeaders from(List<String> headers) {
        Map<String, String> headerPairs = new HashMap<>();

        for (String header : headers) {
            String[] headerProperties = header.split(": ");
            headerPairs.put(headerProperties[KEY_INDEX], headerProperties[VALUE_INDEX]);
        }

        return new RequestHeaders(headerPairs);
    }

    public int getContentLength() {
        if (value.containsKey("Content-Length")) {
            return Integer.parseInt(value.get("Content-Length"));
        }
        return 0;
    }

    public boolean isExistCookie() {
        return value.containsKey("Cookie");
    }

    public HttpCookie getCookie() {
        if (!isExistCookie()) {
            throw new NoSuchElementException();
        }
        return HttpCookie.from(value.get("Cookie"));
    }

    public Map<String, String> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "headers=" + value +
                '}';
    }
}
