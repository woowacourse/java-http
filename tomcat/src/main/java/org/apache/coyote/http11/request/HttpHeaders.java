package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.response.HttpCookie;

public class HttpHeaders {

    private final Map<String, String> values;
    private final HttpCookie cookie;

    private HttpHeaders(Map<String, String> values, HttpCookie cookie) {
        this.values = values;
        this.cookie = cookie;
    }

    public static HttpHeaders from(List<String> headers){
        final Map<String, String> values = new HashMap<>();
        for (final String header : headers) {
            final String[] split = header.split(": ");
            values.put(split[0], split[1]);
        }
        final HttpCookie httpCookie = extractCookie(values);
        return new HttpHeaders(values, httpCookie);
    }

    private static HttpCookie extractCookie(Map<String, String> values) {
        if (values.containsKey("Cookie")) {
            return HttpCookie.from(values.get("Cookie"));
        }
        return HttpCookie.empty();
    }

    public String getHeaderValue(String header) {
        if (!values.containsKey(header)) {
            return "0";
        }
        return values.get(header);
    }

    public Map<String, String> getValues() {
        return values;
    }

    public boolean containsSession() {
        return cookie.containsSession();
    }

    public String getSessionId() {
        return cookie.getSessionId();
    }
}
