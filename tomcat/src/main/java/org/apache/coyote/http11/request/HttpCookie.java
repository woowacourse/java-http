package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private static final String COOKIE_DELIMITER = ";";

    private final Map<String, String> value;

    public HttpCookie(Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie from(String data) {
        Map<String, String> ret = new HashMap<>();
        for (String keyValue : data.split(COOKIE_DELIMITER)) {
            String[] split = keyValue.split("=");
            ret.put(split[0], split[1]);
        }
        return new HttpCookie(ret);
    }

    public String getValue(String key){
        return value.get(key);
    }
}
