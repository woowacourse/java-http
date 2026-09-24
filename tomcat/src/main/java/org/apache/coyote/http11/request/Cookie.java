package org.apache.coyote.http11.request;

public class Cookie {

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

    public String getValue() {
        return value;
    }

    public static String createJSessionId(String sessionId) {
        return JSESSIONID + "=" + sessionId + "; Path=/; HttpOnly; SameSite=Lax";
    }
}
