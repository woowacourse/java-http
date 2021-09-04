package nextstep.jwp.framework.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public void addCookies(final String cookies) {
        final String[] cookie = cookies.split(";");

        for (String pair : cookie) {
            final String[] value = pair.split("=");

            if (value[0].trim().equals("JSESSIONID")) {
                addCookie(value[0].trim(), value[1].trim());
            }
        }
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public String cookies() {
        final StringJoiner line = new StringJoiner("; ");

        for (String key : cookies.keySet()) {
            line.add(key + "=" + cookies.get(key));
        }
        line.add("\r\n");

        return line.toString();
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public boolean hasNotCookie() {
        return cookies.size() == 0;
    }

    @Override
    public String toString() {
        return "Set-Cookie: " + cookies();
    }
}
