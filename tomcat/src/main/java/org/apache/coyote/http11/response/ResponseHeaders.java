package org.apache.coyote.http11.response;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {
    private final Map<String, String> header;

    private ResponseHeaders(Map<String, String> header) {
        this.header = header;
    }

    public static ResponseHeaders create(String path, String resource) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("Content-Type", ContentType.from(path));
        mp.put("Content-Length", String.valueOf(resource.getBytes().length));
        return new ResponseHeaders(mp);
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
