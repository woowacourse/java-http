package nextstep.jwp.web.model;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {

    private static final String JSESSION_ID_NAME = "JSESSIONID";

    private static final HttpCookie EMPTY_HTTP_COOKIE = new HttpCookie(Collections.emptyMap());

    private final Map<String, Cookie> values;

    public HttpCookie(Map<String, Cookie> values) {
        this.values = values;
    }

    public static HttpCookie emptyCookies() {
        return EMPTY_HTTP_COOKIE;
    }

    public boolean hasJSessionId() {
        Cookie sessionCookie = this.values.get(JSESSION_ID_NAME);
        return Objects.nonNull(sessionCookie) && !sessionCookie.getValue().isEmpty();
    }

    public Map<String, Cookie> getValues() {
        return values;
    }

    public String getJSessionId() {
        return this.values.get(JSESSION_ID_NAME).getValue();
    }
}
