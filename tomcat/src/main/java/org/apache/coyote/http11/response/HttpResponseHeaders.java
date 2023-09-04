package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponseHeaders {

    private final Map<String, String> headers;

    private HttpResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders empty() {
        return new HttpResponseHeaders(new HashMap<>());
    }

    public HttpResponseHeaders add(String key, String value) {
        headers.put(key, value);
        return new HttpResponseHeaders(headers);
    }

    public Set<Entry<String, String>> getEntrySet() {
        return headers.entrySet();
    }
}
