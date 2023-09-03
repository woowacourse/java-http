package org.apache.catalina.servlet.session;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.servlet.request.InvalidHeaderException;

public class Cookies {

    private final Map<String, String> cookies = new LinkedHashMap<>();

    private Cookies(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public Cookies() {
    }

    public static Cookies from(String cookies) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] split = cookies.split("; ");
        for (String header : split) {
            String[] nameAndValue = header.split("=");
            validateCookieValue(nameAndValue);
            result.put(nameAndValue[0], nameAndValue[1]);
        }
        return new Cookies(result);
    }

    private static void validateCookieValue(String[] nameAndValue) {
        if (nameAndValue.length != 2) {
            throw new InvalidHeaderException();
        }
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public void put(String name, String value) {
        cookies.put(name, value);
    }

    public Map<String, String> cookies() {
        return cookies;
    }
}
