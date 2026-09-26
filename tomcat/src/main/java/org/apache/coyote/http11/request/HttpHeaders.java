package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> values;
    private final HttpCookie cookie;

    public HttpHeaders(final Map<String, String> values) {
        final Map<String, String> normalized = new HashMap<>();
        values.forEach((name, value) -> normalized.put(name.toLowerCase(Locale.ROOT), value));
        this.values = Map.copyOf(normalized);
        this.cookie = new HttpCookie(this.values.get("cookie"));
    }

    public int contentLength() {
        return Integer.parseInt(getOrDefault("content-length", "0"));
    }

    public boolean isFormUrlEncoded() {
        final String mediaType = getOrDefault("content-type", "").split(";", 2)[0].strip();
        return "application/x-www-form-urlencoded".equalsIgnoreCase(mediaType);
    }

    public String get(final String name) {
        return values.get(name.toLowerCase(Locale.ROOT));
    }

    public String getOrDefault(final String name, final String defaultValue) {
        return values.getOrDefault(name.toLowerCase(Locale.ROOT), defaultValue);
    }

    public HttpCookie cookie() {
        return cookie;
    }
}
