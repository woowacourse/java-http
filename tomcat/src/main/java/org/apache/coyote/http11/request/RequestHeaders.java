package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class RequestHeaders {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
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
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }

    public boolean isExistCookie() {
        return headers.containsKey("Cookie");
    }

    public HttpCookie getCookie() throws IllegalAccessException {
        if (!isExistCookie()) {
            throw new IllegalAccessException();
        }
        return HttpCookie.from(headers.get("Cookie"));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "headers=" + headers +
                '}';
    }
}
