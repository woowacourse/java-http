package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequestBody {

    private Map<String, String> httpRequestBody;

    public HttpRequestBody() {
        this.httpRequestBody = new HashMap<>();
    }

    public void add(String key, String value) {
        this.httpRequestBody.put(key, value);
    }

    public String findBy(String key) {
        return httpRequestBody.get(key);
    }

    public Set<String> keySet() {
        return httpRequestBody.keySet();
    }
}
