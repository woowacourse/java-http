package nextstep.jwp.framework.http.request.details;

import nextstep.jwp.framework.http.common.HttpSession;

import java.util.Map;

public class RequestHttpHeader {

    private static final String COOKIE_HEADER = "Cookie";

    private final Map<String, String> requestHttpHeaderMap;
    private final Cookie cookie;

    private RequestHttpHeader(final Map<String, String> requestHttpHeaderMap, final Cookie cookie) {
        this.requestHttpHeaderMap = requestHttpHeaderMap;
        this.cookie = cookie;
    }

    public static RequestHttpHeader of(final Map<String, String> requestHttpHeader) {
        if (requestHttpHeader.containsKey(COOKIE_HEADER)) {
            final Cookie cookie = Cookie.of(requestHttpHeader.get(COOKIE_HEADER));
            return new RequestHttpHeader(requestHttpHeader, cookie);
        }
        return new RequestHttpHeader(requestHttpHeader, new Cookie());
    }

    public Map<String, String> getRequestHttpHeaderMap() {
        return requestHttpHeaderMap;
    }

    public HttpSession generateSession() {
        return cookie.generateSession();
    }

    public HttpSession getSession() {
        return cookie.getSession();
    }

    public Cookie getCookie() {
        return cookie;
    }
}
