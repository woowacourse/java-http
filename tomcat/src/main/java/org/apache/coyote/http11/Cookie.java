package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cookie {

    private Map<String, String> values;

    private Cookie(final Map<String, String> values) {
        this.values = values;
    }

    public static Cookie from(final String string) {
        final Map<String, String> values = new HashMap<>();
        if (string == null) {
            return new Cookie(values);
        }
        final String[] split = string.split("; ");
        for (String each : split) {
            final String[] keyAndValue = each.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        return new Cookie(values);
    }

    public String getAttribute(final String name) {
        return values.get(name);
    }

    public void setJsessionid() {
        values.put("JSESSIONID", UUID.randomUUID().toString());
    }
}
