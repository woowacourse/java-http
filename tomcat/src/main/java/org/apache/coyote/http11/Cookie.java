package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.coyote.http11.serdes.CookieSerializer;
import org.apache.coyote.http11.serdes.Serializer;

public class Cookie {
    private static String COOKIE_ELEMENT_DELIMITER = ";";
    private static String ELEMENT_DELMITER = "=";

    private final Serializer<Cookie> serializer;
    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.serializer = new CookieSerializer();
        this.cookies = cookies;
    }

    public static Cookie read(String rawValue) {
        Map<String, String> tempCookies = new HashMap<>();
        String[] elements = rawValue.split(COOKIE_ELEMENT_DELIMITER);
        Stream.of(elements)
                .forEach(element -> parseElement(tempCookies, element));
        return new Cookie(tempCookies);
    }

    private static void parseElement(Map<String, String> cookies, String element) {
        String[] parsedElement = element.split(ELEMENT_DELMITER);
        String key = parsedElement[0];
        String value = parsedElement[1];
        cookies.put(key, value);
    }

    public boolean has(String key) {
        return cookies.containsKey(key);
    }

    public void put(String key, String value) {
        cookies.put(key, value);
    }

    public String serialize() {
        return serializer.serialize(this);
    }

    public Map<String, String> getPayLoads() {
        return cookies;
    }
}
