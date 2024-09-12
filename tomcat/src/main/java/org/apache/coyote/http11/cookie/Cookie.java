package org.apache.coyote.http11.cookie;

public class Cookie {
    private String name;
    private String value;
    private Integer maxAge;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String toMessage() {
        String message = "%s=%s; ".formatted(name, value);
        if(maxAge != null) {
            message += "Max-Age=%d".formatted(maxAge);
        }
        return message;
    }
}
