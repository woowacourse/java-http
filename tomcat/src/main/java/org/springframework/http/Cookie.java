package org.springframework.http;

import org.richard.utils.StringUtils;

public class Cookie {

    private final String name;
    private final String value;

    public Cookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static CookieBuilder builder() {
        return new CookieBuilder();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getCookieString() {
        return String.format("%s=%s", this.name, this.value);
    }

    public static class CookieBuilder {

        private String name;
        private String value;

        public Cookie build() {
            checkEssentials();

            return new Cookie(this.name, this.value);
        }

        private void checkEssentials() {
            if (StringUtils.isNullOrBlank(this.name, this.value)) {
                throw new IllegalArgumentException(String.format("Cookie name: %s | value %s", this.name, this.value));
            }
        }

        public CookieBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public CookieBuilder value(final String value) {
            this.value = value;
            return this;
        }
    }
}
