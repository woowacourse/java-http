package org.apache.coyote.http11.serdes;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;

public class CookieSerializer implements Serializer<Cookie> {
    private static final String COOKIE_ELEMENT_DELIMITER = "=";
    private static final String COOKIE_PAYLOAD_DELIMITER = "; ";
    private static final int PARSED_BEGIN_INDEX = 0;

    @Override
    public String serialize(Cookie cookie) {
        Map<String, String> payLoads = cookie.getPayLoads();
        String serializedCookie = payLoads.entrySet()
                .stream()
                .map(this::serializeCookieElement)
                .collect(Collectors.joining());

        return serializedCookie.substring(PARSED_BEGIN_INDEX,
                serializedCookie.length() - COOKIE_PAYLOAD_DELIMITER.length()
        );
    }

    private String serializeCookieElement(Entry<String, String> entry) {
        return entry.getKey()
                + COOKIE_ELEMENT_DELIMITER
                + entry.getValue()
                + COOKIE_PAYLOAD_DELIMITER;
    }
}
