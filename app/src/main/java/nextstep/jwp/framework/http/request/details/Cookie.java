package nextstep.jwp.framework.http.request.details;

import nextstep.jwp.framework.http.request.util.CookieValueExtractor;

import java.util.Map;

public class Cookie {

    private final Map<String, String> cookieValues;

    private Cookie(final Map<String, String> cookieValues) {
        this.cookieValues = cookieValues;
    }

    public static Cookie of(final String cookieHeader) {
        final Map<String, String> cookieValues = CookieValueExtractor.extract(cookieHeader);
        return new Cookie(cookieValues);
    }

    public String searchValue(final String key) {
        return cookieValues.get(key);
    }
}
