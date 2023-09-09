package nextstep.jwp.http.common;

public class Cookie {

    private static final String EMPTY_ATTRIBUTE = "";

    private final String value;
    private final String attribute;

    private Cookie(String value, String attribute) {
        this.value = value;
        this.attribute = attribute;
    }

    public static Cookie from(String value) {
        return new Cookie(value, EMPTY_ATTRIBUTE);
    }

    public static Cookie of(String value, String attribute) {
        return new Cookie(value, attribute);
    }

    public boolean hasAttribute() {
        return !attribute.equals(EMPTY_ATTRIBUTE);
    }

    public String getValue() {
        return value;
    }

    public String getAttribute() {
        return attribute;
    }

}
