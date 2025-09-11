package org.apache.coyote.http11.response.header;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SetCookie extends ResponseHeader {

    private SetCookie(String key, String value) {
        super("Set-Cookie", key + "=" + value);
    }

    public enum SameSite {
        LAX("Lax"),
        STRICT("Strict"),
        NONE("None");

        private final String name;

        SameSite(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Builder {
        private final String key;
        private final String value;
        private final Map<String, String> attributes = new HashMap<>();

        public Builder(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public Builder maxAge(int maxAge) {
            attributes.put("Max-Age", String.valueOf(maxAge));
            return this;
        }

        public Builder path(String path) {
            attributes.put("Path", path);
            return this;
        }

        public Builder expires(ZonedDateTime zonedDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss 'GMT'")
                    .withZone(ZoneId.of("GMT"));
            String formattedDate = formatter.format(zonedDateTime);
            attributes.put("Expires", formattedDate);
            return this;
        }

        public Builder httpOnly() {
            attributes.put("HttpOnly", "");
            return this;
        }

        public Builder secure() {
            attributes.put("Secure", "");
            return this;
        }

        public Builder attribute(String name, String value) {
            attributes.put(name, value);
            return this;
        }

        public Builder sameSite(SameSite sameSite) {
            attributes.put("SameSite", sameSite.getName());
            return this;
        }

        public SetCookie build() {
            return new SetCookie(key, buildCookieString());
        }

        private String buildCookieString() {
            return attributes.entrySet().stream()
                    .map(entry ->
                            entry.getValue().isEmpty() ?
                                    entry.getKey() :
                                    entry.getKey() + "=" + entry.getValue()
                    )
                    .collect(Collectors.joining("; ", value + "; ", ""));
        }
    }
}
