package hoony.was.session;

import org.apache.coyote.http11.HttpCookie;

public class SessionCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final HttpCookie cookie;

    public SessionCookie(String value) {
        this.cookie = new HttpCookie(JSESSIONID, value);
    }

    public String getValue() {
        return cookie.getValue();
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
