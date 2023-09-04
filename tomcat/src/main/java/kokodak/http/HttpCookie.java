package kokodak.http;

import static kokodak.Constants.BLANK;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private Map<String, String> cookie;

    private HttpCookie(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie of(final Map<String, String> header) {
        if (header.containsKey("Cookie")) {
            final Map<String, String> cookie = getCookie(header.get("Cookie"));
            return new HttpCookie(cookie);
        }
        return new HttpCookie(new HashMap<>());
    }

    private static Map<String, String> getCookie(final String cookieHeaderValue) {
        final Map<String, String> cookie = new HashMap<>();
        final String[] cookieSnippets = cookieHeaderValue.split(";" + BLANK.getValue());
        for (final String cookieSnippet : cookieSnippets) {
            final String[] keyValue = cookieSnippet.split("=");
            cookie.put(keyValue[0], keyValue[1]);
        }
        return cookie;
    }

    public String cookie(final String key) {
        if (cookie.containsKey(key)) {
            return cookie.get(key);
        }
        return "";
    }
}
