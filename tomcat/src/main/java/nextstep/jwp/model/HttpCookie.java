package nextstep.jwp.model;

import java.util.Map;
import org.apache.coyote.http11.model.Parameters;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final Parameters parameters) {
        return new HttpCookie(parameters.getParameters());
    }

    public void setCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public String getCookie(final String key) {
        return cookies.get(key);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }
}
