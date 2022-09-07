package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final String cookies) {
        this.cookies = PairConverter.toMap(cookies, "; ", "=");
    }

    public boolean containsKey(final String key) {
        return cookies.containsKey(key);
    }

    public String getJSESSIONID() {
        return cookies.get("JSESSIONID");
    }

    public static String makeJSESSIONID() {
        return UUID.randomUUID().toString();
    }
}
