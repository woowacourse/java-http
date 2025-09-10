package org.apache.coyote.http11.cookie;

public class Cookie {

    private final String name;
    private final String value;
    private boolean httpOnly = true;
    private boolean secure = false;
    private String path = "/";
    private int maxAge = -1;
    private String sameSite = "Lax";

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Cookie httpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public Cookie path(String path) {
        this.path = path;
        return this;
    }

    public Cookie maxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public Cookie secure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public Cookie sameSite(String sameSite) {
        this.sameSite = sameSite;
        return this;
    }

    public String toHeaderValue() {
        StringBuilder sb = new StringBuilder()
                .append(name)
                .append("=")
                .append(value)
                .append("; Path=")
                .append(path);

        if (httpOnly) {
            sb.append("; HttpOnly");
        }
        if (secure) {
            sb.append("; Secure");
        }
        if (maxAge >= 0) {
            sb.append("; Max-Age=").append(maxAge);
        }
        if (sameSite != null) {
            sb.append("; SameSite=").append(sameSite);
        }
        return sb.toString();
    }
}

