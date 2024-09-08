package org.apache.coyote.http11.serdes;

import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.Cookie;

public class CookieSerializer implements Serializer<Cookie> {
    private static final String COOKIE_ELEMENT_DELIMITER = "=";
    private static final String COOKIE_PAYLOAD_DELIMITER = "; ";

    @Override
    public String serialize(Cookie cookie){
        Map<String, String> payLoads = cookie.getPayLoads();
        StringBuilder builder = new StringBuilder(COOKIE_PAYLOAD_DELIMITER);
        payLoads.entrySet()
                .forEach(element -> builder.append(serializeCookieElement(element)));
        return builder.toString();
    }

    private String serializeCookieElement(Entry<String, String> entry) {
        return entry.getKey() + COOKIE_ELEMENT_DELIMITER + entry.getValue();
    }
}
