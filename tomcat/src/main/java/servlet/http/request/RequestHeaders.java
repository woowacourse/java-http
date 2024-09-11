package servlet.http.request;

import java.util.Map;
import java.util.Optional;
import servlet.http.HttpHeader;

public class RequestHeaders {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> headers;

    private final RequestCookies cookies;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
        this.cookies = RequestCookies.from(headers.get(HttpHeader.COOKIE.value()));
    }

    protected String contentLength() {
        return headers.get(HttpHeader.CONTENT_LENGTH.value());
    }

    protected Optional<String> findJSessionId() {
        return Optional.ofNullable(cookies.get(JSESSIONID));
    }
}
