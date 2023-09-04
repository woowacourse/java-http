package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String SEPARATOR = "; ";
    private static final String DELIMITER = "=";

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> items;

    private HttpCookie(Map<String, String> items) {
        this.items = items;
    }

    public static HttpCookie from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return new HttpCookie(new HashMap<>());
        }
        return new HttpCookie(parse(cookieHeader));
    }

    private static Map<String, String> parse(String values) {
        Map<String, String> items = new HashMap<>();
        String[] nameAndValues = values.split(SEPARATOR);
        for (String cookie : nameAndValues) {
            String[] nameAndValue = cookie.split(DELIMITER);
            items.put(nameAndValue[NAME_INDEX], nameAndValue[VALUE_INDEX]);
        }
        return items;
    }

    public boolean hasJSessionId() {
        return items.containsKey(JSESSIONID);
    }

    public void bake() {
        items.put(JSESSIONID, UUID.randomUUID().toString());
    }

    public String getJSessionId() {
        return items.get(JSESSIONID);
    }
}
