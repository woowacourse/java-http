package org.apache.coyote.http11.common;

public class Cookie {

    private static final String EMPTY_ATTRIBUTE = "";
    private static final String ATTRIBUTE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_INDEX = 0;
    private static final int ATTRIBUTE_INDEX = 1;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final String value;
    private final String attribute;

    private Cookie(String value, String attribute) {
        this.value = value;
        this.attribute = attribute;
    }

    public static Cookie from(String line) {
        String[] cookieAndAttribute = line.split(ATTRIBUTE_DELIMITER, 2);
        String[] cookieKeyAndValue = cookieAndAttribute[COOKIE_INDEX].split(KEY_VALUE_DELIMITER);

        if (hasAttribute(line)) {
            return new Cookie(cookieKeyAndValue[COOKIE_VALUE_INDEX], cookieAndAttribute[ATTRIBUTE_INDEX]);
        }

        return new Cookie(cookieKeyAndValue[COOKIE_VALUE_INDEX], EMPTY_ATTRIBUTE);
    }

    private static boolean hasAttribute(String line) {
        return line.contains("; ");
    }

    public static Cookie createWithEmptyAttribute(String value) {
        return new Cookie(value, EMPTY_ATTRIBUTE);
    }

    public static Cookie of(String value, String attribute) {
        return new Cookie(value, attribute);
    }

    public String getValue() {
        return value;
    }

    public String getAttribute() {
        return attribute;
    }

    public boolean hasAttribute() {
        return !EMPTY_ATTRIBUTE.equals(attribute);
    }
}
