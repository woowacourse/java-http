package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class ResponseHeaders {
    private final Map<String, String> header;

    private ResponseHeaders(Map<String, String> header) {
        this.header = header;
    }

    public static ResponseHeaders create(HttpRequest request, String resource) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("Content-Type", ContentType.from(request.getPath()));
        mp.put("Content-Length", String.valueOf(resource.getBytes().length));
        if (request.hasCookie()) {
            mp.putIfAbsent("Set-Cookie", request.getCookie().getResponse());
        }
        return new ResponseHeaders(mp);
    }

    public String getCookieValue() {
        return header.get("Set-Cookie");
    }

    public void put(String key, String value) {
        header.put(key, value);
    }

    public String get(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
