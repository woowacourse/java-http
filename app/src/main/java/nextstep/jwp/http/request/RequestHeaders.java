package nextstep.jwp.http.request;

import com.google.common.base.Strings;
import nextstep.jwp.http.session.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.http.session.HttpCookie.COOKIE_NAME;

public class RequestHeaders {

    private static final String DELIMITER = ": ";
    private final Map<String, String> headers = new HashMap<>();
    private HttpCookie cookie = new HttpCookie();

    public void put(String line) {
        String[] tokens = line.split(DELIMITER);
        if (COOKIE_NAME.equals(tokens[0])) {
            this.cookie = HttpCookie.of(tokens[1]);
        }
        this.headers.put(tokens[0].trim(), tokens[1].trim());
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (Strings.isNullOrEmpty(contentLength)) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean hasSessionId() {
        return cookie.hasSessionId();
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(cookie.getSessionId());
    }
}
