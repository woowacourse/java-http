package common.http;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final String SEPARATOR = "; ";
    public static final String DELIMITER = "=";

    private static final int ATTRIBUTE_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> items;

    private Cookie(Map<String, String> items) {
        this.items = items;
    }

    public static Cookie from(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return new Cookie(new HashMap<>());
        }
        return new Cookie(parse(cookieHeader));
    }

    private static Map<String, String> parse(String values) {
        Map<String, String> items = new HashMap<>();
        String[] attributesAndValues = values.split(SEPARATOR);
        for (String cookie : attributesAndValues) {
            String[] attributeAndValue = cookie.split(DELIMITER);
            items.put(attributeAndValue[ATTRIBUTE_INDEX], attributeAndValue[VALUE_INDEX].trim());
        }
        return items;
    }

    public void addAttribute(String attribute, String value) {
        items.put(attribute, value);
    }

    boolean hasAttribute(String attribute) {
        return items.containsKey(attribute);
    }

    String getAttribute(String attribute) {
        return items.get(attribute);
    }

    public String getValue() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> item : items.entrySet()) {
            stringBuilder.append(item.getKey()).append(DELIMITER).append(item.getValue()).append(SEPARATOR);
        }
        String cookie = stringBuilder.toString();
        return cookie.substring(0, cookie.length()-2);
    }
}
