package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpCookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(List<String> headerList) {
        final Map<String, String> map = new HashMap<>();
        for (String str : headerList) {
            final String[] parts = str.split(":");
            if (parts.length == 2) {
                final String key = parts[0].trim();
                final String value = parts[1].trim();
                map.put(key, value);
            }
        }
        return new RequestHeader(map);
    }

    public boolean hasJSessionId() {
        System.out.println("RequestHeader까지 들어옴");
        if (!headers.containsKey("Set-Cookie")) {
            System.out.println("Set-Cookie가 없음");
            return false;
        }
        return HttpCookie.from(headers.get("Set-Cookie")).hasJSessionId();
    }
}
