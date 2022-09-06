package nextstep.jwp.http.request;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpRequestHeaders {

    private static final String REQUEST_HEADER_DELIMIETER = ": ";
    private static final int REQUEST_HEADER_KEY_INDEX = 0;
    private static final int REQUEST_HEADER_VALUE_INDEX = 1;

    private static final String CONTENT_LENGTH_KEY = "Content-Length";
    private static final String COOKIE_KEY = "Cookie";
    private static final String EMPTY_COOKIE = "";

    private final Map<String, String> values;

    public HttpRequestHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpRequestHeaders from(final List<String> requestHeaders) {
        return new HttpRequestHeaders(requestHeaders.stream()
                .map(requestHeader -> requestHeader.split(REQUEST_HEADER_DELIMIETER))
                .collect(Collectors.toMap(e -> e[REQUEST_HEADER_KEY_INDEX], e -> e[REQUEST_HEADER_VALUE_INDEX])));
    }

    public boolean isContainContentLength() {
        return values.containsKey(CONTENT_LENGTH_KEY);
    }

    public int contentLength() {
        if (!isContainContentLength()) {
            throw new UncheckedServletException("content가 없습니다.");
        }
        return Integer.parseInt(values.get(CONTENT_LENGTH_KEY));
    }

    public boolean isContainCookie() {
        return values.containsKey(COOKIE_KEY);
    }

    public String getCookie() {
        if (!isContainCookie()) {
            return EMPTY_COOKIE;
        }
        return values.get(COOKIE_KEY);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpRequestHeaders that = (HttpRequestHeaders) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
