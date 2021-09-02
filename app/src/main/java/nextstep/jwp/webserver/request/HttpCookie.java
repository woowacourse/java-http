package nextstep.jwp.webserver.request;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.webserver.util.HttpRequestParser;

public class HttpCookie {

    private static final String COOKIE_REGEX = ";";

    private final Map<String, String> cookies;

    public HttpCookie(String cookieLine) {
        if(cookieLine == null || cookieLine.isEmpty()) {
            this.cookies = new HashMap<>();
            return;
        }
        this.cookies = saveWithParsing(cookieLine);
    }

    private Map<String, String> saveWithParsing(String cookieLine) {
        return HttpRequestParser.parseValues(cookieLine, COOKIE_REGEX);
    }

    public String getValue(String key) {
        return this.cookies.get(key);
    }
}
