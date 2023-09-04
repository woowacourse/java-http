package org.apache.coyote.http.vo;

import util.MultiValueMap;

public class HttpBody {

    private final MultiValueMap<String, String> body;

    private HttpBody(final MultiValueMap<String, String> body) {
        this.body = body;
    }

    public void put(final String key, final String value) {
        body.put(key, value);
    }

    public String getValue(final String key) {
        return body.getRecentValue(key);
    }

    public static HttpBody getEmptyBody() {
        return new HttpBody(new MultiValueMap<>());
    }
}
