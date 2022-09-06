package org.apache.coyote.http11.session;

public class Cookie {

    private static final String NAME_VALUE_DELIMITER = "=";
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String name;
    private final String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @Return Cookie instance from cookie format string (ex. JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46)
     */
    public static Cookie from(String cookieFormatString) {
        String[] components = cookieFormatString.split(NAME_VALUE_DELIMITER);
        return new Cookie(components[NAME_INDEX], components[VALUE_INDEX]);
    }

    public String headerValue() {
        return String.format("%s" + NAME_VALUE_DELIMITER + "%s", name, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
