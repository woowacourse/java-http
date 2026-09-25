package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpCookie {

    private static final String DELIMITER = ";";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String cookieHeader) {
        return new HttpCookie(ParameterParser.parse(cookieHeader, DELIMITER));
    }

    public String get(final String name) {
        return values.get(name);
    }

    public String getJsessionid() {
        return values.get(JSESSIONID);
    }
}
