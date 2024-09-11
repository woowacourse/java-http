package org.apache.coyote.http;

public class Cookie implements HttpComponent {

    public static final String JSESSIONID = "JSESSIONID";

    private final String name;
    private final String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String asString() {
        return name + "=" + value;
    }
}
