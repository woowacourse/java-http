package nextstep.jwp.http.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseCookie {
    
    private final Map<String, String> cookies;

    public ResponseCookie() {
        this.cookies = new ConcurrentHashMap<>();
    }

    public void add(String key, String value) {
        cookies.put(key, value);
    }

    public List<String> getAsSetCookieString() {
        final List<String> cookiesToSet = new ArrayList<>();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookiesToSet.add("Set-Cookie: " + entry.getKey() + "=" + entry.getValue() + " ");
        }
        return cookiesToSet;
    }
}
