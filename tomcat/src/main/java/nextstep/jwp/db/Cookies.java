package nextstep.jwp.db;

import static org.apache.coyote.Constants.JSESSIONID;

public class Cookies {

    public static HttpCookie ofJSessionId(String id) {
        return HttpCookie.of(JSESSIONID + id);
    }
}
