package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.HttpResponse.CRLF;

public class Headers {

    private final Map<String, String> headers;

    public Headers() {
        this(new HashMap<>());
    }

    public Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public String join() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining(CRLF));
    }

    public void addCookie(Cookies cookies) {

    }

    public void setCookie(String key, String value) {
        if (headers.containsKey("Set-Cookie")) {
            headers.put("Set-Cookie", headers.get("Set-Cookie") + "; " + key + "=" + value);
            return;
        }
        headers.put("Set-Cookie", key + "=" + value);
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }


}
