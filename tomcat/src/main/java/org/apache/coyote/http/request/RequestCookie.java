package org.apache.coyote.http.request;

import java.util.Map;
import org.apache.coyote.util.StringParser;

public class RequestCookie {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String FIELD_DELIMITER = "; ";

    private final Map<String, String> cookies;

    private RequestCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static RequestCookie from(final String cookieHeaderValue) {
        final Map<String, String> cookies = StringParser.split(cookieHeaderValue, FIELD_DELIMITER, KEY_VALUE_DELIMITER);
        return new RequestCookie(cookies);
    }

    public boolean hasSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getSessionId() {
        return cookies.get("JSESSIONID");
    }
}
