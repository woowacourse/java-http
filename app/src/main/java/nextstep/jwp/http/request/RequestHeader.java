package nextstep.jwp.http.request;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;

public class RequestHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> header;
    private final HttpCookie httpCookie;

    public RequestHeader() {
        this(new ConcurrentHashMap<>(), new HttpCookie());
    }

    public RequestHeader(Map<String, String> header, HttpCookie httpCookie) {
        this.header = header;
        this.httpCookie = httpCookie;
    }

    public void setHeader(String line) {
        String[] splitLine = line.split(HEADER_DELIMITER);
        String key = splitLine[0];
        String value = splitLine[1];

        if (COOKIE.equals(key)) {
            setCookie(value);
        }
        header.put(key, value);
    }

    private void setCookie(String value) {
        String[] splitCookies = value.split(COOKIES_DELIMITER);

        for (String each : splitCookies) {
            String[] splitCookie = each.split(COOKIE_DELIMITER);
            httpCookie.setStorage(splitCookie[0], splitCookie[1]);
        }
    }

    public boolean isContentLength() {
        return header.containsKey(CONTENT_LENGTH);
    }

    public boolean isJSessionId() {
        return httpCookie.isJSessionId();
    }

    public String getValue(String key) {
        return header.get(key);
    }

    public String getJSessionId() {
        return httpCookie.getJSessionId();
    }

    public HttpSession getSession() {
        if (httpCookie.isJSessionId()) {
            return HttpSessions.getSession(httpCookie.getJSessionId());
        }
        return null;
    }

    public void saveSession(HttpSession httpSession) {
        HttpSessions.saveSession(httpSession);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
