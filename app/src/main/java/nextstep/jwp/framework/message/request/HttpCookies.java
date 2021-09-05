package nextstep.jwp.framework.message.request;

import nextstep.jwp.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpCookies {

    private static final String COOKIE_PIECE_SEPARATOR = ";";
    private static final String COOKIE_PARAM_SEPARATOR = "=";

    private final Map<String, String> params;

    public HttpCookies(Map<String, String> params) {
        this.params = params;
    }

    public static HttpCookies from(String cookieString) {
        return new HttpCookies(
                StringUtils.extractMap(cookieString, COOKIE_PIECE_SEPARATOR, COOKIE_PARAM_SEPARATOR)
        );
    }

    public Optional<String> take(String key) {
        return Optional.ofNullable(params.get(key));
    }

    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpCookies that = (HttpCookies) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
