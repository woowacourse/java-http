package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http.exception.InvalidCookieContentException;
import org.apache.coyote.http.util.HttpConsts;

public class HttpCookie {

    public static final HttpCookie EMPTY = new HttpCookie(Collections.emptyMap());
    public static final String SESSION_ID_KEY = "JSESSIONID";

    private static final String HEADER_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int VALID_HEADER_TOKEN_SIZE = 2;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private static final String SET_COOKIE_HEADER_KEY = "Set-Cookie: ";

    private final Map<String, String> headers;

    private HttpCookie(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpCookie fromSessionId(final String sessionId) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(SESSION_ID_KEY, sessionId);

        return new HttpCookie(headers);
    }

    public static HttpCookie fromCookieHeaderValue(final String cookieHeaderContents) {
        if (cookieHeaderContents == null) {
            throw new InvalidCookieContentException();
        }

        final Map<String, String> headers = Arrays.stream(cookieHeaderContents.split(HEADER_DELIMITER))
                                                  .map(String::strip)
                                                  .map(headerContent -> headerContent.split(KEY_VALUE_DELIMITER))
                                                  .collect(Collectors.toMap(
                                                          contentTokens -> contentTokens[HEADER_KEY_INDEX],
                                                          HttpCookie::convertHeaderValue
                                                  ));
        return new HttpCookie(headers);
    }

    private static String convertHeaderValue(final String[] headerTokens) {
        if (headerTokens.length < VALID_HEADER_TOKEN_SIZE) {
            return HttpConsts.BLANK;
        }

        return headerTokens[HEADER_VALUE_INDEX];
    }

    public String findValue(final String key) {
        return headers.get(key);
    }

    public String convertHeaders() {
        final String setCookieValue = headers.entrySet().stream()
                                             .map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue() + HEADER_DELIMITER + HttpConsts.SPACE)
                                             .collect(Collectors.joining(HttpConsts.CRLF));

        return SET_COOKIE_HEADER_KEY + setCookieValue;
    }
}
