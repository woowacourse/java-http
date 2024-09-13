package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
        String key = parsedElement[0].trim();
        String value = parsedElement[1].trim();
        cookies.put(key, value);
    }

    public boolean has(String key) {
        return cookies.containsKey(key);
    }

    public String getByKey(String key){
        if(!has(key)){
            throw new NoSuchElementException(key + " 를 가진 cookie 값이 없습니다.");
        }
        return cookies.get(key);
    }

    public String serialize() {
        return serializer.serialize(this);
    }

    public Map<String, String> getPayLoads() {
        return cookies;
    }
}
