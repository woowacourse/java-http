package org.apache.coyote.common.header;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookie {

    public static final String SESSION_ID_COOKIE_KEY = "JSESSIONID";

    private static final String ATTRIBUTE_KEY_VALUE_DELIMITER = "=";
    private static final String ATTRIBUTE_DELIMITER = "; ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> parsedCookie;

    public Cookie(final String rawCookie) {
        this.parsedCookie = parseRawCookie(rawCookie);
    }

    private Map<String, String> parseRawCookie(final String rawCookie) {
        if (rawCookie.isEmpty()) {
            return new HashMap<>();
        }
        String[] cookiePairs = rawCookie.split(ATTRIBUTE_DELIMITER);
        return Arrays.stream(cookiePairs)
                .filter(cookiePair -> cookiePair.contains(ATTRIBUTE_KEY_VALUE_DELIMITER))
                .map(cookiePair -> cookiePair.split(ATTRIBUTE_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(cookiePair -> cookiePair[KEY_INDEX], cookiePair -> cookiePair[VALUE_INDEX]));
    }

    public static SetCookieBuilder setCookieBuilder(final String key, final String value) {
        return new SetCookieBuilder(key, value);
    }

    public Optional<String> getValue(final String key) {
        return Optional.ofNullable(parsedCookie.get(key));
    }

    public String generateSessionIdCookie() {
        final String cookieValue = parsedCookie.get(SESSION_ID_COOKIE_KEY);
        if (cookieValue == null) {
            final String sessionId = UUID.randomUUID().toString();
            parsedCookie.put(SESSION_ID_COOKIE_KEY, sessionId);
            return String.join(ATTRIBUTE_KEY_VALUE_DELIMITER,
                    SESSION_ID_COOKIE_KEY,
                    parsedCookie.get(SESSION_ID_COOKIE_KEY));
        }
        return cookieValue;
    }

    public static class SetCookieBuilder {

        private static final String EXPIRES = "expires";
        private static final String DOMAIN = "domain";
        private static final String PATH = "path";
        private static final String SECURE = "secure";
        private static final String HTTP_ONLY = "HttpOnly";

        private final String key;
        private final String value;

        private String expires;
        private String domain;
        private String path;
        private String secure;
        private String httpOnly;

        SetCookieBuilder(final String key, final String value) {
            this.key = key;
            this.value = value;
            try {
                this.domain = makeAttribute(DOMAIN, InetAddress.getLocalHost()
                        .getHostName());
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Unknown local host name.");
            }
            this.path = makeAttribute(PATH, "\\");
            expires = "";
            secure = "";
            httpOnly = HTTP_ONLY;
        }

        public SetCookieBuilder setExpires(final int maxAgeSeconds) {
            final LocalDateTime expiresDateTime = LocalDateTime.now()
                    .plusSeconds(maxAgeSeconds);
            expires = makeAttribute(EXPIRES, expiresDateTime.getDayOfWeek()
                    .name()
                    + ", "
                    + ZonedDateTime.of(expiresDateTime, ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("dd-MM-yy hh:mm:ss"))
                    + "GMT");
            return this;
        }

        public SetCookieBuilder setDomain(final String domain) {
            this.domain = makeAttribute(DOMAIN, domain);
            return this;
        }

        public SetCookieBuilder setPath(final String path) {
            this.path = makeAttribute(PATH, path);
            return this;
        }

        private String makeAttribute(final String attributeKey, final String attributeValue) {
            return String.join(ATTRIBUTE_KEY_VALUE_DELIMITER, attributeKey, attributeValue);
        }

        public SetCookieBuilder setSecure() {
            this.secure = SECURE;
            return this;
        }

        public SetCookieBuilder disableHttpOnly() {
            this.httpOnly = "";
            return this;
        }

        public String asString() {
            return Stream.of(makeAttribute(key, value), expires, domain, path, secure, httpOnly)
                    .filter(attribute -> attribute != null && !attribute.isEmpty())
                    .collect(Collectors.joining(ATTRIBUTE_DELIMITER));
        }
    }
}
